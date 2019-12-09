#!/usr/bin/env bash

set -e

if [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  if [ ! -z "$TRAVIS_TAG" ]; then
    echo "Deploying release"
    gpg --version
    gpg --import ${GPG_DIR}/gpg.asc
    mvn clean deploy --settings travis/mvn-settings.xml -B -U -P oss-release "$@" -DskipTests=true
  else
    echo "Deploying snapshot"
    mvn clean deploy --settings travis/mvn-settings.xml -B -U "$@" -DskipTests=true
  fi
fi