#!/bin/bash

if [ "$CI" = "true" ] && [ "$TRAVIS" = "true" ];
then
  echo "Running on Travis CI"
  PR="$TRAVIS_PULL_REQUEST"
fi

if [ "$PR" != "false" ];
then
  echo "Running on pull request $PR"
  wget "https://philanthropist.touk.pl/nexus/service/local/artifact/maven/redirect?r=snapshots&g=pl.touk&a=sputnik&c=all&v=LATEST" -O sputnik.jar && java -jar sputnik.jar --conf sputnik.properties --pullRequestId $PR
fi

