#!/bin/bash
#
# Deploy a jar, source jar, and javadoc jar to Sonatype's snapshot repo.
#
# Adapted from https://coderwall.com/p/9b_lfq and
# http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

SLUG="Tickaroo/tikxml"
JDK="oraclejdk8"
BRANCH="master"

set -e
echo "starting deploy snapshot script"

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying ..."
  openssl aes-256-cbc -K $encrypted_abf389171084_key -iv $encrypted_abf389171084_iv -in .buildscript/private.key.enc -out private.key -d
  gpg --import private.key
  echo "SONATYPE_NEXUS_USERNAME=$CI_DEPLOY_USERNAME" >> gradle.properties
  echo "SONATYPE_NEXUS_PASSWORD=$CI_DEPLOY_PASSWORD" >> gradle.properties
  echo "NEXUS_USERNAME=$CI_DEPLOY_USERNAME" >> gradle.properties
  echo "NEXUS_PASSWORD=$CI_DEPLOY_PASSWORD" >> gradle.properties
  echo "signing.keyId=E1FB7CBA" >> gradle.properties
  echo "signing.password=$PGP_KEY" >> gradle.properties
  echo "signing.secretKeyRingFile=/home/travis/.gnupg/secring.gpg" >> gradle.properties
  echo "org.gradle.parallel=false" >> gradle.properties
  echo "org.gradle.configureondemand=false" >> gradle.properties
  ./gradlew uploadArchives
  rm private.key
  git reset --hard
  echo "Deployed!"
fi
