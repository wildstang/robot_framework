#!/bin/bash

# pull-upstream.sh
# this script pulls the given branch from the given upstream repo

GITHUB="ssh://git@github.com:wildstang"
REMOTE="temp-upstream"

# check parameters
if [ $# -lt 2 ]; then
    echo "./pull-upstream.sh [repo] [branch]"
    exit 1
fi

repo=$1
branch=$2

git remote add $REMOTE "$GITHUB/$repo"
git pull $REMOTE $branch
git remote remove $REMOTE

echo "This script does not push changes. Please inspect local remote before pushing."
