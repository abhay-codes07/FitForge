import json
import sqlite3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SCHEMA_PATH = ROOT / 'app' / 'schemas' / 'com.fitforge.app.data.local.db.FitForgeDatabase' / '2.json'
OUTPUT_PATH = ROOT / 'app' / 'src' / 'main' / 'assets' / 'databases' / 'exercises.db'
SEPARATOR = '\u001f'
ESCAPE = '\\'
NOW = 1735689600000

CATEGORY_TEMPLATES = [
    {
        'category': 'strength',
        'equipment': ['dumbbell'],
        'muscles': [('chest', 'triceps'), ('back', 'biceps'), ('shoulders', 'traps'), ('quads', 'glutes'), ('hamstrings', 'glutes')],
        'patterns': ['press', 'row', 'raise', 'lunge', 'hinge'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'bodyweight',
        'equipment': ['bodyweight'],
        'muscles': [('chest', 'triceps'), ('back', 'rear_delts'), ('core', 'obliques'), ('quads', 'glutes'), ('hamstrings', 'glutes')],
        'patterns': ['push', 'pull', 'plank', 'squat', 'bridge'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'cardio',
        'equipment': ['treadmill'],
        'muscles': [('cardio', 'legs'), ('cardio', 'core'), ('cardio', 'shoulders')],
        'patterns': ['interval', 'tempo', 'steady'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'mobility',
        'equipment': ['mat'],
        'muscles': [('hips', 'glutes'), ('thoracic_spine', 'shoulders'), ('ankles', 'calves')],
        'patterns': ['flow', 'reach', 'rotation'],
        'difficulties': ['beginner', 'intermediate'],
    },
    {
        'category': 'yoga',
        'equipment': ['mat'],
        'muscles': [('core', 'hips'), ('shoulders', 'hamstrings'), ('glutes', 'hamstrings')],
        'patterns': ['sun', 'balance', 'power'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'boxing',
        'equipment': ['gloves', 'bag'],
        'muscles': [('shoulders', 'core'), ('chest', 'triceps'), ('cardio', 'core')],
        'patterns': ['jab', 'combo', 'conditioning'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'cycling',
        'equipment': ['bike'],
        'muscles': [('quads', 'calves'), ('hamstrings', 'glutes'), ('cardio', 'legs')],
        'patterns': ['climb', 'interval', 'endurance'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
    {
        'category': 'running',
        'equipment': ['track'],
        'muscles': [('quads', 'calves'), ('hamstrings', 'glutes'), ('cardio', 'core')],
        'patterns': ['sprint', 'tempo', 'endurance'],
        'difficulties': ['beginner', 'intermediate', 'advanced'],
    },
]

VARIATIONS = ['alpha', 'beta', 'gamma']


def encode_collection(items):
    encoded_items = []
    for item in items:
        item = item.replace(ESCAPE, ESCAPE + ESCAPE).replace(SEPARATOR, ESCAPE + SEPARATOR)
        encoded_items.append(item)
    return SEPARATOR.join(encoded_items)


def titleize(value):
    return value.replace('_', ' ').title()


def build_exercises():
    exercises = []
    counter = 1
    for template in CATEGORY_TEMPLATES:
        for primary, secondary in template['muscles']:
            for pattern in template['patterns']:
                for difficulty in template['difficulties']:
                    for variation in VARIATIONS:
                        name = f"{titleize(primary)} {titleize(pattern)} {variation.title()}"
                        description = (
                            f"A {difficulty} {template['category']} drill focused on {titleize(primary)} "
                            f"with support from {titleize(secondary)}."
                        )
                        instructions = [
                            f"Set up for the {name.lower()} with controlled posture.",
                            f"Drive effort through {titleize(primary).lower()} while stabilizing with {titleize(secondary).lower()}.",
                            'Maintain consistent breathing and smooth tempo until the interval ends.',
                        ]
                        duration_seconds = 30 + ((counter % 8) * 15)
                        calories = 20 + ((counter % 7) * 8)
                        is_premium = 1 if counter % 9 == 0 else 0
                        equipment = list(template['equipment'])
                        if template['category'] in {'strength', 'bodyweight'} and counter % 4 == 0:
                            equipment.append('bench')
                        if template['category'] in {'mobility', 'yoga'} and counter % 5 == 0:
                            equipment.append('band')
                        exercise = (
                            f"exercise_{counter:03d}",
                            name,
                            description,
                            template['category'],
                            difficulty,
                            encode_collection(equipment),
                            encode_collection([primary]),
                            encode_collection([secondary]),
                            encode_collection(instructions),
                            duration_seconds,
                            calories,
                            None,
                            None,
                            is_premium,
                            'asset_seed',
                            NOW + counter,
                            NOW + counter,
                        )
                        exercises.append(exercise)
                        counter += 1
                        if len(exercises) >= 216:
                            return exercises
    return exercises


def main():
    schema = json.loads(SCHEMA_PATH.read_text(encoding='utf-8'))
    db_schema = schema['database']

    if OUTPUT_PATH.exists():
        OUTPUT_PATH.unlink()

    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    connection = sqlite3.connect(OUTPUT_PATH)
    try:
        cursor = connection.cursor()
        cursor.execute('PRAGMA foreign_keys=OFF')
        cursor.execute(f"PRAGMA user_version={db_schema['version']}")

        for statement in db_schema['setupQueries']:
            cursor.execute(statement)

        for entity in db_schema['entities']:
            cursor.execute(entity['createSql'].replace('${TABLE_NAME}', entity['tableName']))
            for index in entity.get('indices', []):
                cursor.execute(index['createSql'].replace('${TABLE_NAME}', entity['tableName']))

        exercises = build_exercises()
        cursor.executemany(
            '''
            INSERT INTO exercises (
                id, name, description, category, difficulty, equipment, primaryMuscles,
                secondaryMuscles, instructions, estimatedDurationSeconds, estimatedCalories,
                imageUrl, videoUrl, isPremium, source, createdAtEpochMillis, updatedAtEpochMillis
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''',
            exercises,
        )
        connection.commit()
        print(f'Wrote {len(exercises)} exercises to {OUTPUT_PATH}')
    finally:
        connection.close()


if __name__ == '__main__':
    main()
