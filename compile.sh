#!/usr/bin/env bash
# Script to compile ISAcreator source code.
# It first downloads the configuration files, which are required withint the tool.

SKIP_TESTS=true

while getopts c:t option
do
        case "${option}"
        in
                t) SKIP_TESTS=false;;
                c) CONFIG=${OPTARG};;
        esac
done

echo "Value of SKIP_TESTS ---", $SKIP_TESTS

rm -rf src/main/resources/Configurations
mkdir src/main/resources/Configurations

if hash curl 2>/dev/null; then
   echo "curl is installed, will download configurations next"
else
   echo "curl is not installed, install it and then run compile.sh again"
   exit 1
fi

CONFIGURATION=isaconfig-default_v2015-07-02.zip

curl -L -O http://bitbucket.org/eamonnmag/isatools-downloads/downloads/"$CONFIGURATION"

mv $CONFIGURATION src/main/resources/Configurations/

WD=$(pwd)

pwd
cd src/main/resources/Configurations/
unzip $CONFIGURATION
rm -f $CONFIGURATION
pwd
cd $WD
echo "Changing back to target..."
pwd


mvn $MVNOPTS -Dmaven.test.skip=$SKIP_TESTS clean assembly:assembly -Pbuild


