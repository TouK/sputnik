if [ "$CI" = "true" ] && [ "$TRAVIS" = "true" ];
then
  echo "Running on Travis CI"
  PR="$TRAVIS_PULL_REQUEST"
fi

if [ ! -z "$PR" ];
then
  echo "Running on pull request $PR"
  wget "https://philanthropist.touk.pl/nexus/service/local/artifact/maven/redirect?r=Snapshots&g=pl.touk&a=sputnik&classifier=all&v=LATEST" -O sputnik.jar && java -jar sputnik.jar --conf sputnik.properties --pullRequestId $PR
fi

