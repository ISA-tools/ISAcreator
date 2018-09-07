#!/bin/bash
# Use this file to build all the packages about ISA-Tools
# More details in the POM. 
#

# usage:
#    ./package.sh <PACKAGE_TYPE>
#
# The possible values for <PACKAGE_TYPE> are (use them without the quotes):
#   - 'scidata': to build a package of ISAcreator including the Scientific Data ISA-Tab configurations
#   - 'mixs': to build a package of ISAcreator including the MIXS ISA-Tab configurations
#   - 'miacme': to build a package of ISAcreator including the MIACME ISA-Tab configurations
#   - 'all': to build a package of ISAcreator including the default ISA-Tab configurations
#
# If no <PACKAGE_TYPE> parameter is passed, the default value 'all' will be used.
#
# e.g. ./package.sh scidata
#

PACKAGE_TYPE=$1
echo "ISAcreator packaging for type " $PACKAGE_TYPE

MIXS_DATASETS=mixs-datasets-v2.zip

if [ "$PACKAGE_TYPE" = ""  ]
then
    PACKAGE_TYPE="all"
fi

#MVNOPTS="--offline"
get_tag_data () {
    local tag=$1
    local xml_file=$2

    # Find tag in the xml, convert tabs to spaces, remove leading spaces, remove the tag.
    grep $tag $xml_file | \
        tr '\011' '\040' | \
        sed -e 's/^[ ]*//' \
            -e 's/^<.*>\([^<].*\)<.*>$/\1/'

}

ALLVERSIONS=`get_tag_data \<version\> pom.xml  2>/dev/null`

VERSIONSPLIT=(`echo $ALLVERSIONS | tr ' ' ' '`)
# get first version number referenced. Should always be the version of the project, since it's always the first
VERSION=${VERSIONSPLIT[0]}

if [ "$VERSION" = "" ]
then
  echo "Couldn't extract version from pom.xml. Exiting."
  exit 1
fi

rm -rf src/main/resources/Configurations

mkdir src/main/resources/Configurations

if [ "$PACKAGE_TYPE" = "scidata"  ]
then
    CONFIGURATION=isaconfig-Scientific-Data-v1.2.zip
else
     if [ "$PACKAGE_TYPE" = "mixs"  ]
     then
        CONFIGURATION=isaconfig-mixs-v4.zip
    else
        if [ "$PACKAGE_TYPE" = "miacme"  ]
        then
            CONFIGURATION=isaconfig-MIACMEv0.3-multiassay-20180725.zip
        else
            CONFIGURATION=isaconfig-default_v2015-07-02.zip
        fi
    fi
fi

echo "Configuration file: " $CONFIGURATION

if hash curl 2>/dev/null; then
   echo "curl is installed, will download configurations next"
else
   echo "curl is not installed, install it and then run package.sh again"
   exit 1
fi

if [ "$PACKAGE_TYPE" = "miacme"  ]
then
    curl -L -O https://bitbucket.org/agbeltran/isatools-downloads/downloads/"$CONFIGURATION"
else
    curl -L -O http://bitbucket.org/eamonnmag/isatools-downloads/downloads/"$CONFIGURATION"
fi

mkdir Configurations
cp $CONFIGURATION
cd Configurations
unzip $CONFIGURATION
rm -f $CONFIGURATION
cd ..

#back to project folder

cp $CONFIGURATION src/main/resources/Configurations/

#project folder
WD=$(pwd)

pwd
cd src/main/resources/Configurations/
unzip $CONFIGURATION
rm -f $CONFIGURATION
pwd

cd $WD
echo "Changing back to project folder..."
pwd

###installing xalan dependency
mvn dependency:get -Dartifact=xalan:xalan:2.4.0
mvn install:install-file -Dfile="$HOME/.m2/repository/xalan/xalan/2.4.0/xalan-2.4.0.jar" -DgroupId=xalan -DartifactId=xalan -Dversion=2.4 -Dpackaging=jar


##Building ISAcreator
mvn $MVNOPTS -Dmaven.test.skip=true clean assembly:assembly -Pbuild

if [ "$?" -ne 0 ]; then
    echo "Maven Build Unsuccessful!"
    exit 1
fi

mkdir target/Configurations
cp $CONFIGURATION target/Configurations/
rm -f $CONFIGURATION

cd target/Configurations/
unzip $CONFIGURATION
rm -f $CONFIGURATION

cd ..

#in target folder

ISATAB_FOLDER = "isatab files"

# Now package up the tools
mkdir "$ISATAB_FOLDER"
if [ "$PACKAGE_TYPE" = "scidata" ]
then
    cd "$ISATAB_FOLDER"
    curl -L -O https://bitbucket.org/eamonnmag/isatools-downloads/downloads/SciData-Datasets-1-and-2.zip
    unzip SciData-Datasets-1-and-2.zip
    rm -f SciData-Datasets-1-and-2.zip
    cd ../
fi
if [ "$PACKAGE_TYPE" = "mixs" ]
then
    cd "$ISATAB_FOLDER"
    curl -L -O https://bitbucket.org/eamonnmag/isatools-downloads/downloads/$MIXS_DATASETS
    unzip $MIXS_DATASETS
    rm -f $MIXS_DATASETS
    cd ../
else
     cp -r ../src/test/resources/test-data/ ./"$ISATAB_FOLDER"
fi

pwd

zip --exclude .DS_STORE -r ISAcreator-$VERSION-$PACKAGE_TYPE.zip Configurations ../ProgramData "$ISATAB_FOLDER"
mv ISAcreator-$VERSION-jar-with-dependencies.jar ISAcreator.jar
zip -u ISAcreator-$VERSION-$PACKAGE_TYPE.zip ISAcreator.jar

python ../bundler.py

echo "Packaging completed successfully!"

# Creates the folder we then package up as a DMG.
mkdir ISAcreator-$VERSION-dmg-folder/
mkdir ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
mv ISAcreator.app ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r ../src/main/resources/Configurations ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r ../ProgramData ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r "$ISATAB_FOLDER" ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION