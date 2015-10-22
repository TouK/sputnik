#!/usr/bin/env bash
# check arguments
for param in current_branch stash_password sputnik_distribution_url stash_user; do
    if [ -z "${!param}" ]; then
        echo "Required parameter '$param' is missing! "
        exit 1
    fi
done

case "$current_branch" in
    master|production|release*)
        echo "Not checking master or production or release branch, exiting."
        exit
        ;;
esac

configFile="sputnik.properties"
if [ ! -e "$configFile" ]; then
    echo "No $configFile present, exiting. "
    exit 1
fi

getProperty() {
    file=$1
    propertyKey=$2
    echo `grep -v '^[[:space:]]*\#' $file | grep "$propertyKey" | tail -1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
}

getStashBaseUrl() {
    https=$(getProperty "$configFile" "connector.useHttps")
    host=$(getProperty "$configFile" "connector.host")
    port=$(getProperty "$configFile" "connector.port")
    path=$(getProperty "$configFile" "connector.path")
    if [ "$https" == "true" ]; then
        scheme="https"
    else
        scheme="http"
    fi
    echo "$scheme://$host:$port$path"
}

project_key=$(getProperty "$configFile" "connector.projectKey")
repository_slug=$(getProperty "$configFile" "connector.repositorySlug")
stash_base_url=$(getStashBaseUrl)
pull_requests_url="$stash_base_url/rest/api/1.0/projects/$project_key/repos/$repository_slug/pull-requests"

tmp_dir=`mktemp -d`

tmp_output="$tmp_dir/json"
curl -s $pull_requests_url -u $stash_user:$stash_password > $tmp_output

pyexec() {
    echo "`/usr/bin/python -c 'import sys; exec sys.stdin.read()'`"
}

pullRequestId() {
    pyexec <<END
# -*- coding: utf-8 -*-
import json
from pprint import pprint
json_data=open('$tmp_output')

data = json.load(json_data)
json_data.close()

for pr in data["values"]:
    if pr["fromRef"]["displayId"] == u"$current_branch":
        print pr["id"]
END
}

prId=`pullRequestId`

## download sputnik
wget -O "$tmp_dir/sputnik.zip" $sputnik_distribution_url
cd $tmp_dir
unzip sputnik.zip
cd -

## filter properties file
sed -i -e "s/<username>/$stash_user/; s/<password>/$stash_password/" $configFile

## run code analysis
$tmp_dir/sputnik-*/bin/sputnik \
    --conf sputnik.properties \
    --pullRequestId $prId

## cleanup
rm -R $tmp_dir
