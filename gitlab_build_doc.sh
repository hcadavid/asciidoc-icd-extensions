#!/bin/bash

cd $(dirname $0)

mvn -e -DASCIIDOC_SOURCE_PATH=$1 -DOUTPUT_PATH=$2 -DBACKEND_URL=$3 -DBACKEND_CREDENTIALS=$4 -DPROJECT_NAME=$CI_PROJECT_NAME -DPIPELINE_ID=$CI_PIPELINE_ID -DDEPLOYMENT_URL=$CI_PAGES_URL -DSOURCE_URL=$CI_PROJECT_URL  -DCOMMIT_AUTHOR="'$CI_COMMIT_AUTHOR'" -DCREATION_DATE=$CI_PIPELINE_CREATED_AT -DCOMMIT_TAG=$CI_COMMIT_TAG -DLOG_TAGS_FILE_PATH=$PWD/logs.txt

ec=$?
if [[ $ec -eq 0 ]] ;  then
  echo Success - Error code 0  
else
  echo 'Build failed - check logging details in the dashboard' 
  exit $ec 
fi

cd -
