#!/usr/bin/env bash

set -e

CWD=$(pwd)

# we will use Community ids to download plugins.
SCALA_PLUGIN_ID="org.intellij.scala"
PYTHON_PLUGIN_ID="PythonCore"
FULL_IJ_BUILD_NUMBER="IC-${IJ_BUILD_NUMBER}"

IJ_BUILD="IC-${IJ_VERSION}"
if [[ $IJ_ULTIMATE == "true" ]]; then
  IJ_BUILD="IU-${IJ_VERSION}"
fi

mkdir -p .cache/intellij

if [ ! -d .cache/intellij/idea-dist ]; then
  echo "Loading $IJ_BUILD..."
  wget http://download-cf.jetbrains.com/idea/idea${IJ_BUILD}.tar.gz
  tar zxf idea${IJ_BUILD}.tar.gz
  rm -rf idea${IJ_BUILD}.tar.gz
  UNPACKED_IDEA=$(find . -name 'idea-I*' | head -n 1)
  mv "$UNPACKED_IDEA" ".cache/intellij/idea-dist"
fi