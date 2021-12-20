#!/bin/bash

mvn -DASCIIDOC_SOURCE_PATH=$1 -DOUTPUT_PATH=$2 $3 $4
ec=$?
if [[ $ec -eq 0 ]] ;  then
  echo Success - Error code 0  
else
  echo 'Build failed - check logging details in the dashboard' 
  exit $ec 
fi
