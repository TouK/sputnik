#!/bin/bash

if [ "$CI" = "true" ] && [ "$TRAVIS" = "true" ];
then
  echo "Running on Travis CI"
  PR="$TRAVIS_PULL_REQUEST"
fi

if [ "$PR" != "false" ];
then
  echo "Running on pull request $PR"
  if [ ! -z "$api_key" ];
  then
    echo "Downloading sputnik.properties"
    wget -q "http://sputnik.touk.pl/conf/$TRAVIS_REPO_SLUG/sputnik-properties?key=$api_key" -O sputnik.properties
  fi
  wget "https://philanthropist.touk.pl/nexus/service/local/artifact/maven/redirect?r=snapshots&g=pl.touk&a=sputnik&c=all&v=LATEST" -O sputnik.jar && java -jar sputnik.jar --conf sputnik.properties --pullRequestId $PR
fi

