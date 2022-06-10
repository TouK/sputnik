![sputnik](http://touk.github.io/sputnik/images/logo-color-bgtransparent-small.png)

> Static code review for your Gerrit and Stash patchsets. Runs Checkstyle, PMD, SpotBugs (formerly known as FindBugs), Scalastyle, CodeNarc, JSLint, JSHint, TSLint and Detekt for you!

[![Build Status](https://img.shields.io/travis/TouK/sputnik/master.svg?style=flat-square)](https://travis-ci.org/TouK/sputnik)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.touk/sputnik/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/pl.touk/sputnik)
[![Coverage Status](https://img.shields.io/codecov/c/github/TouK/sputnik/master.svg?style=flat-square)](https://codecov.io/github/TouK/sputnik)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/TouK/sputnik)
[![Join the chat at https://gitter.im/TouK/sputnik](https://badges.gitter.im/TouK/sputnik.svg)](https://gitter.im/TouK/sputnik?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

Sputnik runs Checkstyle, PMD, SpotBugs, CodeNarc, JSHint (or JSLint), TSLint, Ktlint and Detekt only on files affected by Gerrit's patchset. It collects all violations and report them back to Gerrit or Stash.

Typical configuration file looks like this:

```properties
connector.type=gerrit
connector.host=your.host.com
connector.path=/gerrit
connector.port=8080
connector.username=sputnik
connector.password=PassWd
connector.useHttps=false
connector.verifySsl=false
connector.tag=sputnik
checkstyle.enabled=true
checkstyle.configurationFile=sun_checks.xml
checkstyle.propertiesFile=
pmd.enabled=true
pmd.ruleSets=rulesets/java/android.xml,rulesets/java/basic.xml
spotbugs.enabled=true
spotbugs.includeFilter=
spotbugs.excludeFilter=
codenarc.enabled=true
codenarc.ruleSets=
codenarc.excludes=**/*.java
jslint.enabled=false
jshint.enabled=true
jshint.configurationFile=jshint.json
tslint.enabled=true
tslint.script=/usr/bin/tslint
tslint.configurationFile=tslint.json
ktlint.enabled=true
ktlint.exclude=no-semi,indent
detekt.enabled=true
detekt.config.file=src/test/resources/detekt/config/config.yml
```

See [this](src/main/resources/example.properties) for a downloadable sample file, and [this](src/main/java/pl/touk/sputnik/configuration/GeneralOption.java) for a full list of options.

If you want sputnik to use your SonarQube rules just download them from your SonarQube profile and use these with `checkstyle.configurationFile`, `pmd.ruleSets` and `spotbugs.includeFilter` variables.

To ease migration from FindBugs to SpotBugs these three configuration properties still work and affect SpotBugs now: `findbugs.enabled`, `findbugs.includeFilter` and `findbugs.excludeFilter`.

## Installation

- clone this repository and build it: `gradle distZip` or download [distribution](https://github.com/TouK/sputnik/releases/download/sputnik-2.6.0/sputnik-2.6.0.zip)
- copy distribution file `build/distributions/sputnik-2.6.0.zip` to your installation dir, e.g. `/opt/sputnik` and unzip it
- to avoid problems with deployment keep the structure unchanged, so sputnik file is in `bin/` directory, jars in `lib/`
- create configuration file (you can just paste and edit an example above), e.g. `/opt/sputnik/myconf.properties`
- you can now run sputnik like this:
```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/gerrit.properties --changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

### Build tool

Sputnik is intended to run just after your Jenkins/CI server build. It should be executed in the root directory of the analyzed project to find files to analyze.

Sputnik currently supports Maven (default) and Gradle. Some processors (e.g. SpotBugs) analyze compiled classes, so it's important to set
the build tool property correctly. To change it to Gradle just set `project.build.tool=gradle` in your `sputnik.properties` file.

### Gerrit support

Three parameters are required: your configuration file (details below), Gerrit's changeId and revisionId. ie:

```
sputnik --conf /path/to/conf.properties --changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

Other parameters are available. See them with `sputnik --help`.

Depending on your workflow it can happen that the changeId matches changes on multiple branches, for example when you push a change set identified by particular changeId into your working branch and review branch.\
You can recognize it by the following error message:

```
Request not successful. Message: Not Found. Status-Code: 404. Content: Not found: yours_change_id
```

In this case the extended changeId format (`REPO_NAME~BRANCH_NAME~CHANGE_ID`) should be used which includes the repository and the branch name, for example:

```
sputnik --conf /path/to/conf.properties --changeId myProject~master~I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

### Stash support

If you choose to run sputnik with Stash instead of Gerrit, you'll need to run it in the following manner:

```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/stash.properties --pullRequestId 15
```

It is convenient to add sputnik's configuration file (`myconf.properties` in the above example) to your
project's repo. This way, it will be easier to run it from CI server.

### SSL verification
SSL trust verification and hostname verification is disabled by default. You can enable it by setting `connector.verifySsl=true` property. 

### Add Post-Build step to Jenkins/CI server

If you have Jenkins job that uses Gerrit Trigger plugin it's very easy to integrate it as Post-Build step:

- create a user in Gerrit with HTTP password access and Non-Interactive Users group (take a look at Gerrit documentation [https://git.eclipse.org/r/Documentation/cmd-create-account.html](here))
- add Post-Build step to your Jenkins job: Execute bash shell:
```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/myconf.properties --changeId $GERRIT_CHANGE_ID --revisionId $GERRIT_PATCHSET_REVISION
# This line makes sure that this Post-Build step always returns exit code 0
# so it won't affect your main build result
echo "exit 0 workaround"
```

### Add Post-Build step to Bamboo

When stash is build on Bamboo there is no direct way to check which pull
request id it matches. This is a simple way to find required id.

Assumptions:
- there is sputnik's config file named `sputnik.properties` in project's root directory
- user and password are configured in bamboo plan as variables (e.g.
  _ecosystem.username_ and _ecosystem.password_)
- config file has placeholders for user and password:
```properties
stash.username=<username>
stash.password=<password>
```

With those steps in place you can use a step from
`contrib/stash-execute.sh`:

```
current_branch=${bamboo.repository.branch.name} sputnik_distribution_url=https://github.com/TouK/sputnik/releases/download/sputnik-1.4.0/sputnik-1.4.0.zip stash_password=${bamboo_ecosystem_password} stash_user=${bamboo_ecosystem_username} ./stash-execute.sh
```

## Launching with Maven

If you prefer running Sputnik from Maven, there is a plugin developed by Karol Lassak here: https://github.com/ingwarsw/sputnik-maven-plugin. Read plugin documentation for reference.

## Launching with Gradle

If you prefer running Sputnik from Gradle all you need is to have Gradle installed.
Put build.gradle file in your repository, add config file and run:
```
gradle run -Dexec.args="--conf example.properties --changeId 1234 --revisionId 4321"
```

## Requirements

- Gerrit 2.8 is required (REST API for reviews was introduced in this version)
- Jenkins or other CI server to download and build patchsets

## Contributors

- Tomasz Kalkosiński
- Marcin Cylke
- Piotr Jagielski
- Karol Lassak
- Henning Hoefer
- Dominik Przybysz
- Damian Szczepanik
- Rafał Nowak
- Filip Majewski

## License

This project is licenced under Apache License.

