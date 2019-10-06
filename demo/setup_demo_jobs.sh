#!/bin/bash
# just load up 9 jobs in the schema
CRONICLE="http://localhost:8080/crons"
cd `dirname $0`
for i in `seq 1 9`; do
  cronexpr="$i,1$i,2$i,3$i,4$i,5$i * * * *"
  curl -s -X POST -H "Content-Type: application/json" -d \
    "{\"name\":\"demo-job-0${i}\",\"description\":\"Demo Job 0${i}\",\"expression\":\"0 $cronexpr\",\"timezone\":\"Asia/Calcutta\",\"gracePeriodForStart\":5,\"maxRuntime\":-1}" \
    $CRONICLE  -o "demo-job-0${i}.json"
  key=`jq -r ".key" demo-job-0${i}.json`
  echo "$cronexpr `pwd`/cronicle-client-wrapper.sh $key `pwd`/demo-cron.sh > `pwd`/demo-cron.sh.0${i}.log"
  rm -f demo-job-0${i}.json
done

echo "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
echo "Add the lines above to your crontab entry file"
