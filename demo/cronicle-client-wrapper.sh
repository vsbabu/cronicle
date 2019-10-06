#!/bin/bash
#
# Wrapper shell to run your cron job 
# Put your cron command like `thiscommand GUID yourcommand`
#
set -e
CRONICLE="http://localhost:8080/job"
JOBID=$1
shift
CMD=$1
shift
curl $CRONICLE/$JOBID/start
set +e
$CMD $*
if  [ $? -eq 0 ]; then
  curl $CRONICLE/$JOBID/pass
else
  curl $CRONICLE/$JOBID/fail
fi
