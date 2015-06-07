#!/bin/bash

#---------------------------------------------------
#
# If specified in application.properties, an event
# handler will be called with event name, cron id
# and name as arguments.
#
# you can use that to send sms, mail etc for things
# you care about.
#
#
# this is a sample script to get you started
#---------------------------------------------------

EVENT_NAME=$1
CRON_ID=$2
CRON_NAME=$3

# do something here - sample below
{
case $EVENT_NAME in

  FAILED) echo "RUN Failed for $CRON_NAME"
    ;;

  DID_NOT_RUN) echo "$CRON_NAME never ran"
    ;;

  RAN_TOO_LONG) echo "$CRON_NAME took too long to run"
    ;;

  *)
    ;;
esac
} 2>&1 >> /tmp/cronevents.log
