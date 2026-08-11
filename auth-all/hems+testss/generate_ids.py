# Simple script to generate 100 random IDs
import random

# Generate 100 random 8-digit IDs
ids = []
for _ in range(100):
    ids.append(str(random.randint(10000000, 99999999)))

# Write to file
with open('depts_ids.txt', 'w') as f:
    f.write('#random 100 ids\n')
    for id in ids:
        f.write(id + '\n')

print('Done! Generated 100 random IDs.')