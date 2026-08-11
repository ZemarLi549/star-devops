# Simple script to generate 99 random event IDs
import random

# Generate 99 random 8-digit event IDs
event_ids = []
for _ in range(99):
    event_ids.append(str(random.randint(10000000, 99999999)))

# Write to file
with open('events_ids.txt', 'w') as f:
    f.write('#random 99 event_id\n')
    for event_id in event_ids:
        f.write(event_id + '\n')

print('Done! Generated 99 random event IDs.')