#!/bin/bash

# a sample that simply sleeps for a random time, prints some info and exits
# with failure status on odd seconds

MAX_SLEEP=10
SLEEP=$((RANDOM % MAX_SLEEP))
sleep $SLEEP
EXIT_STATUS=$((`date "+%S"` % 2))
echo "PID => $$, SLEEP => $SLEEP, STATUS => $EXIT_STATUS"
exit $EXIT_STATUS
