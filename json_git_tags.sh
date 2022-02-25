#!/bin/bash
echo Extracrting git tags from git repository in folder $1
git --git-dir $1/.git  tag -l --format='{"tag": "%(tag)", "subject": "%(subject)", "created": "%(creatordate)"}' > $2
