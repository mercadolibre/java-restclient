#!/bin/bash

function isCommitted() {
    git diff-index --quiet HEAD --
}

function getBranch() {
    git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/\1/'
}

function isPushed() {
    branch=$(getBranch)
    changes=$(git log --quiet origin/${branch}..${branch})

    if [ "$changes" == "" ]; then
        return 0
    else
        return 1
    fi
}

function updateVersion() {
   mvn -B versions:set -DnewVersion=$1
}

function deploy() {
    mvn deploy
}

function updateReadmeVersion() {
    line_nbr=$(getReadmeVersionLineNbr restclient-default)
    modifyVersion $line_nbr $1

    line_nbr=$(getReadmeVersionLineNbr restclient-core)
    modifyVersion $line_nbr $1
}

function getReadmeVersionLineNbr() {
    awk "/<artifactId>$1<\/artifactId>/{getline; print NR}" README.md
}

function modifyVersion() {
    line_nbr=$1
    version=$2

    if [ "$line_nbr" != "" ]; then
        sed -i "${line_nbr}s/<version>.*<\/version>/<version>$version<\/version>/" README.md
    fi
}

function updateGit() {
    git commit -am "RestClient new version: $1"
    git push origin $(getBranch)
}

function print_help() {
    echo "Usage: build.sh [-v version] [-d] [-h]"
}

version=""
do_deploy=0
while getopts ":v:dh" flag; do
	case $flag in
	  v) version=$OPTARG;;
	  d) do_deploy=1;;
	  h) print_help; exit 0;;
	  \? | *) print_help; exit 2;;
	esac
done
shift $(( OPTIND - 1 ));

if [ "$version" == "" ]; then
    echo "Version is mandatory"
    print_help
    exit 1;
elif ! isCommitted; then
    echo "Repo has uncommitted changes"
    exit 1
elif ! isPushed; then
    echo "Branch has non pushed changes"
    exit 1
fi

#updateVersion $version
updateReadmeVersion $version

if [ $do_deploy -eq 1 ]; then
    deploy
    updateGit $version
fi
