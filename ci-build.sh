#!/bin/bash
ideaVersion="14.1"
if [ ! -d ./idea-IC ]; then
# Get our IDEA dependency
if [ -f ~/Tools/ideaIC-${ideaVersion}.tar.gz ];
then
cp ~/Tools/ideaIC-${ideaVersion}.tar.gz .
else
wget http://download.jetbrains.com/idea/ideaIC-${ideaVersion}.tar.gz
fi
# Unzip IDEA
tar zxf ideaIC-${ideaVersion}.tar.gz
rm -rf ideaIC-${ideaVersion}.tar.gz
# Move the versioned IDEA folder to a known location
ideaPath=$(find . -name 'idea-IC*' | head -n 1)
mv ${ideaPath} ./idea-IC
fi


# Run Ant Build
if [ "$1" = "-d" ]; then
ant -d -f build.xml -DIDEA_HOME=./idea-IC
else
ant -f build.xml -DIDEA_HOME=./idea-IC
fi
# get build status
stat=$?
if [ "${TRAVIS}" != true ]; then
ant -f build.xml -q clean
#rm -rf idea-IC
fi
# return build status
exit ${stat}