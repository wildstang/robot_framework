#!/bin/bash

# fork.sh
# this script forks the framework at a given branch into a given existing (but empty) WildStang repo
# it then adds a tag to this repo

UPSTREAM="git@github.com:wildstang/robot_framework.git"
GITHUB="git@github.com:wildstang"
UPSTREAM_BRANCH="main"

fork=$1
branch=$2
push_changes=$3

# check parameters
if [ $# -lt 1 ]; then
    echo "Fork repository name is required"
    exit 1
elif [ $# -lt 2 ]; then
    branch=$UPSTREAM_BRANCH
fi

# update to upstream
if [ $fork == "update" ]; then
    git remote add upstream $UPSTREAM
    git pull upstream $branch
    if [ $push_changes == "push" ]; then
        git push
    fi
    exit 0
fi

# clone then enter the upstream repo
git clone $UPSTREAM framework_fork
cd framework_fork

# add a remote for the new repo then push to it
git remote add fork "${GITHUB}/${fork}"
git push fork $branch:$UPSTREAM_BRANCH

# check for common errors
error=$?
if [ $error -eq 128 ]; then
    echo ""
    echo "Repo \"${GITHUB}/${fork}\" could not be found"
    cd ..
    rm -rf framework_fork
    exit 2
elif [ $error -eq 1 ]; then
    echo ""
    echo "Branch \"${branch}\" could not be found"
    cd ..
    rm -rf framework_fork
    exit 3
fi

# add tag to framework
git tag -a $fork -m "wildstang/${fork} forked from here"
git push origin $fork

# remove old repo, then clone the new repo
cd ..
rm -rf framework_fork
git clone "${GITHUB}/${fork}"
cd $fork

# update year across the repo, pull the year from the beginning of the new repo name
if [[ $fork =~ ^20[0-9]{2}_ ]]; then
    year=$(echo $fork | cut -c1-4)

    # don't overwrite up-to-date gradle versions
    if ! grep -q "\"${year}." build.gradle; then
        # update gradle version
        sed -i "s/20[0-9]\{2\}\.[0-9]\.[0-9]/${year}.1.1/" build.gradle
    fi

    # update frcYear for gradle
    sed -i "s/20[0-9]\{2\}/${year}/" settings.gradle
    # update projectYear for wpilib
    sed -i "s/20[0-9]\{2\}/${year}/" .wpilib/wpilib_preferences.json

    # automatically push year update
    git add --all
    git commit -m "[fork.sh] Updated project year to ${year}"
    if [ $push_changes == "push" ]; then
        git push
    fi

    # make new year20XX package
    mv src/main/java/org/wildstang/sample "src/main/java/org/wildstang/year${year}"
    # rename package in all files
    grep -rlF "wildstang.sample" "src/main/java/org/wildstang/year${year}" | xargs sed -i "s/wildstang.sample/wildstang.year${year}/g"
    # update package name for gradle ROBOT_MAIN_CLASS
    sed -i "s/sample/year${year}/" build.gradle

    # automatically push year directory
    git add --all
    git commit -m "[fork.sh] Created year${year} package"
    if [ $push_changes == "push" ]; then
        git push
    fi
else
    echo "Year not found in repo name"
fi