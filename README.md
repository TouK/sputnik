# sputnik

> Static code review for your Gerrit patchsets. Runs Checkstyle, PMD and FindBugs for you!

[![Build Status](https://api.travis-ci.org/TouK/sputnik.png)](https://travis-ci.org/TouK/sputnik)
[![Coverage Status](https://coveralls.io/repos/TouK/sputnik/badge.png?branch=master)](https://coveralls.io/r/TouK/sputnik?branch=master)

## Usage

Sputnik is intended to run just after your Jenkins/CI server build. It should be executed in that workspace directory to find files to analyse.

Three parameters are required: your configuration file (details below), Gerrit's changeId and revisionId:

```
sputnik -conf /home/spoonman/sputnik/conf.properties -changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 -revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

Sputnik runs Checkstyle, PMD and FindBugs only on files affected by Gerrit's patchset. It collects all violations and report them back to Gerrit.

Typical configuration file looks like this:

```
connector.type=gerrit
connector.host=your.host.com
connector.path=gerrit/
connector.port=8080
connector.username=sputnik
connector.password=PassWd
checkstyle.enabled=true
checkstyle.configurationFile=sun_checks.xml
checkstyle.propertiesFile=
pmd.enabled=true
pmd.ruleSets=rulesets/java/android.xml,rulesets/java/basic.xml
findbugs.enabled=true
findbugs.includeFilter=
findbugs.excludeFilter=
```

If you want sputnik to use your SonarQube rules just download them from your SonarQube profile and use these with `checkstyle.configurationFile`, `pmd.ruleSets` and `findbugs.includeFilter` variables.

## Installation

- clone this repository and build it: `gradle distZip` or download distribution file: https://github.com/TouK/sputnik/releases/download/v1.0/sputnik-1.0.zip
- copy distribution file `build/distributions/sputnik-1.0.zip` to your installation dir, e.g. `/opt/sputnik` and unzip it
- create configuration file (you can just paste and edit an example above), e.g. `/opt/sputnik/myconf.properties`
- you can now run sputnik like this:
```
/opt/sputnik/sputnik-1.0/bin/sputnik -conf /opt/sputnik/gerrit.properties -changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 -revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

### Stash support

If you choose to run sputnik with Stash instead of Gerrit, you'll need to run it in the following manner:

```
/opt/sputnik/sputnik-1.0/bin/sputnik --conf /opt/sputnik/stash.properties -pullRequestId 15
```

It is convenient to add sputnik's configuration file (`myconf.properties` in the above example) to your
project's repo. This way, it will be easier to run it from CI server.

### Add Post-Build step to Jenkins/CI server

If you have Jenkins job that uses Gerrit Trigger plugin it's very easy to integrate it as Post-Build step:

- create a user in Gerrit with HTTP password access and Non-Interactive Users group (take a look at Gerrit documentation [https://git.eclipse.org/r/Documentation/cmd-create-account.html][here])
- add Post-Build step to your Jenkins job: Execute bash shell:
```
/opt/sputnik/sputnik-1.0/bin/sputnik -conf /opt/sputnik/myconf.properties -changeId $GERRIT_CHANGE_ID -revisionId $GERRIT_PATCHSET_REVISION
# This line makes sure that this Post-Build step always returns exit code 0
# so it won't affect your main build result
echo "exit 0 workaround"
```

### Add Post-Build step to Bamboo

TODO

## Requirements

- Gerrit 2.8 is required (REST API for reviews was introduced in this version)
- Jenkins or other CI server to download and build patchsets

## Contributors

- Tomasz Kalkosiński
- Marcin Cylke
- Piotr Jagielski
- Karol Lassak

## License

This project is licenced under Apache License.

