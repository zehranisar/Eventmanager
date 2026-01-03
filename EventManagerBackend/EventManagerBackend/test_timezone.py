"""
Quick test script to verify timezone conversion
Run this with: python test_timezone.py
"""

from datetime import datetime, timedelta, timezone as dt_timezone

# Test: Event at 11:40 PM PKT, reminder 1 hour before
event_time_pkt = datetime(2025, 1, 15, 23, 40, 0)  # 11:40 PM
print(f"Event time (PKT): {event_time_pkt}")

# Convert to UTC (subtract 5 hours)
event_time_utc_naive = event_time_pkt - timedelta(hours=5)
event_time_utc = event_time_utc_naive.replace(tzinfo=dt_timezone.utc)
print(f"Event time (UTC): {event_time_utc}")

# Calculate reminder (1 hour before)
reminder_utc = event_time_utc - timedelta(hours=1)
print(f"Reminder time (UTC): {reminder_utc}")

# Convert back to PKT to verify
pakistan_tz = dt_timezone(timedelta(hours=5))
reminder_pkt = reminder_utc.astimezone(pakistan_tz)
print(f"Reminder time (PKT): {reminder_pkt}")

print("\nExpected results:")
print("Event time (PKT): 2025-01-15 23:40:00")
print("Event time (UTC): 2025-01-15 18:40:00+00:00")
print("Reminder time (UTC): 2025-01-15 17:40:00+00:00")
print("Reminder time (PKT): 2025-01-15 22:40:00+05:00")

