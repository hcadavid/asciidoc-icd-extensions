#!/bin/bash
cd $(dirname $0)

mvn -e -DASCIIDOC_SOURCE_PATH=$1 -DOUTPUT_PATH=$2 -DBACKEND_URL=$3 -DVALE_LINTER_OUTPUT_PATH=$VALE_LINTER_OUTPUT_PATH

ec=$?
if [[ $ec -eq 0 ]] ;  then
  echo Success - Error code 0  
else
  echo 'Build failed - check logging details in the dashboard' 
  exit $ec 
fi

cd -
