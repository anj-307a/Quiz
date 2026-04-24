# Quiz Leaderboard System

## Overview

This project is based on an API integration task where quiz data is fetched multiple times and used to build a leaderboard.

The main challenge is that the API can return the same data more than once, so we need to make sure duplicate entries are not counted.

---

## What the program does

* Calls the API 10 times (poll values 0 to 9)
* Waits 5 seconds between each call
* Collects all events
* Removes duplicate entries using `(roundId + participant)`
* Calculates total score for each participant
* Sorts them to form a leaderboard
* Submits the final result

---

## How duplicates are handled

Each event is identified using a unique key:
```

A `HashSet` is used to store these keys.
If the same event appears again, it is skipped.

---

## How scores are calculated

A `HashMap` is used to store scores:

* Key → participant name
* Value → total score

Whenever a new (non-duplicate) event is found, the score is added.

---

## Leaderboard

After processing all polls:

* Data is converted into a list
* Sorted in descending order of score

## Notes

* No external libraries were used
* JSON parsing is handled manually for simplicity
* The program may take around 50 seconds to complete due to delays

---

## Conclusion

This task mainly tests handling of duplicate API data and correct aggregation. Once duplicates are handled properly, the rest of the logic is straightforward.

