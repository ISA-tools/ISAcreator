#!/usr/bin/env bash
# Script to compile ISAcreator source code.
# It first downloads the configuration files, which are required withint the tool.

SKIP_TESTS=true
CONFIG="default"

while getopts h,help,config:,c:,t,tests option
do
        case "${option}"
        in
                t,tests) SKIP_TESTS=false;;
                c,config) CONFIG=${OPTARG};;
        esac
done

if [ "$CONFIG" = "scidata"  ]
then
    CONFIG_FILES=isaconfig-Scientific-Data-v1.2.zip
else
     if [ "$CONFIG" = "mixs"  ]
     then
        CONFIG_FILES=isaconfig-mixs-v4.zip
    else
        if [ "$CONFIG" = "default"  ]
        then
            CONFIG_FILES=isaconfig-default_v2015-07-02.zip
        fi
    fi
fi


rm -rf src/main/resources/Configurations
mkdir src/main/resources/Configurations

if hash curl 2>/dev/null; then
   echo "curl is installed, will download configurations next"
else
   echo "curl is not installed, install it and then run compile.sh again"
   exit 1
fi


curl -L -O http://bitbucket.org/eamonnmag/isatools-downloads/downloads/"$CONFIG_FILES"

mkdir Configurations
cp $CONFIG_FILES ./Configurations
cd Configurations
unzip $CONFIG_FILES
rm -f $CONFIG_FILES
cd ..

mv $CONFIG_FILES src/main/resources/Configurations/

WD=$(pwd)

pwd
cd src/main/resources/Configurations/
unzip $CONFIG_FILES
rm -f $CONFIG_FILES
pwd
cd $WD
echo "Changing back to project folder..."
pwd

mvn $MVNOPTS -Dmaven.test.skip=$SKIP_TESTS clean assembly:assembly -Pbuild


mkdir "isatab files"
if [ "$CONFIG" = "scidata" ]
then
    cd "isatab files"
    curl -L -O https://bitbucket.org/eamonnmag/isatools-downloads/downloads/SciData-Datasets-1-and-2.zip
    unzip SciData-Datasets-1-and-2.zip
    rm -f SciData-Datasets-1-and-2.zip
    cd ../
fi
if [ "$CONFIG" = "mixs" ]
then
    cd "isatab files"
    curl -L -O https://bitbucket.org/eamonnmag/isatools-downloads/downloads/$MIXS_DATASETS
    unzip $MIXS_DATASETS
    rm -f $MIXS_DATASETS
    cd ../
else
    cp -r src/test/resources/test-data/ "isatab files"/
fi


