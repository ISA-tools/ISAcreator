#!/bin/sh
# Use this file to build all the packages about ISA-Tools
# More details in the POM. 
#

# should be used by passing in either 'scidata' or 'all' as a parameter, e.g. ./package.sh scidata
# switching these will result in different actions being performed on packaging.

PACKAGE_TYPE=$1
echo $PACKAGE_TYPE

if [ "$PACKAGE_TYPE" = ""  ]
then
    PACKAGE_TYPE="all"
fi

alias mvn='/Users/eamonnmaguire/dev/maven/bin/mvn'
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
fi


rm -rf src/main/resources/Configurations

mkdir src/main/resources/Configurations

if [ "$PACKAGE_TYPE" = "scidata"  ]
then
    CONFIGURATION=isaconfig-Scientific-Data-v1.1.zip
else
    CONFIGURATION=isaconfig-default_v2014-01-16.zip
fi

wget https://bitbucket.org/eamonnmag/isatools-downloads/downloads/"$CONFIGURATION" --no-check-certificate

cp $CONFIGURATION src/main/resources/Configurations/


WD=$(pwd)

pwd
cd src/main/resources/Configurations/
unzip $CONFIGURATION

rm -f $CONFIGURATION
pwd
cd $WD
echo "Changing back to target..."
pwd

mvn $MVNOPTS -Dmaven.test.skip=true clean assembly:assembly -Pbuild


mkdir target/Configurations
cp $CONFIGURATION target/Configurations/
rm -f $CONFIGURATION

cd target/Configurations/
unzip $CONFIGURATION
rm -f $CONFIGURATION

# Now package up the tools
cd ../

mkdir "isatab files"
if [ "$PACKAGE_TYPE" = "scidata" ]
then
    cd "isatab files"
    wget https://bitbucket.org/eamonnmag/isatools-downloads/downloads/SciData-Datasets-1-and-2.zip --no-check-certificate
    unzip SciData-Datasets-1-and-2.zip
    rm -f SciData-Datasets-1-and-2.zip
    cd ../
else
    cp -r ../"isatab files"/* "isatab files"
fi

pwd

zip --exclude .DS_STORE -r ISAcreator-$VERSION-$PACKAGE_TYPE.zip Configurations ../ProgramData "isatab files"
mv ISAcreator-$VERSION-jar-with-dependencies.jar ISAcreator.jar
zip -u ISAcreator-$VERSION-$PACKAGE_TYPE.zip ISAcreator.jar

python ../bundler.py

echo "Packaging completed successfully!"

# Creates the folder we then package up as a DMG.
mkdir ISAcreator-$VERSION-dmg-folder/
mkdir ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
mv ISAcreator.app ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r Configurations ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r ../ProgramData ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION
cp -r "isatab files" ISAcreator-$VERSION-dmg-folder/ISAcreator-$VERSION

