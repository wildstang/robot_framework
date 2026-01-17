#!/bin/bash

# push-upstream.sh
# this script pushes the given branch to the given upstream repo

GITHUB="ssh://git@github.com/wildstang"
REMOTE="temp-upstream"

# check parameters
if [ $# -lt 2 ]; then
    echo "./push-upstream.sh [repo] [branch]"
    exit 1
fi

repo=$1
branch=$2

git remote add $REMOTE "$GITHUB/$repo"
git push $REMOTE $branch
git remote remove $REMOTE
