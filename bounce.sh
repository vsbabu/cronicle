#!/bin/bash

#
# Helper script to bounce the server
#
#  To be changed to better stuff with log4j
#
. $HOME/.bash_profile
WORKDIR=`dirname $0`
cd $WORKDIR
PIDFILE="${WORKDIR}/app.pid"
kill `cat $PIDFILE`
mv nohup.out  nohup.`date +%Y-%m-%d_%H%M%S`.out
rm -f nohup.out 
if [ $# -eq 0 ]; then
	# if any argument is given, just kill the server
	nohup bin/cronicle > nohup.out 2>&1 &
	echo $! > $PIDFILE
else
	exit
fi
bzip2 nohup.2*.out
