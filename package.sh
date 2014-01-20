#!/bin/sh
# Use this file to build all the packages about ISA-Tools
# More details in the POM. 
#

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

if [ "$VERSION" = "" ] ; then 
  echo "Couldn't extract version from pom.xml. Exiting."
fi
  

rm -rf Configurations
rm -rf src/main/resources/Configurations

CONFIGURATION=isaconfig-default_v2014-01-16.zip

mkdir Configurations
mkdir src/main/resources/Configurations
cd Configurations
wget https://bitbucket.org/eamonnmag/isatools-downloads/downloads/"$CONFIGURATION" --no-check-certificate
cp $CONFIGURATION ../src/main/resources/Configurations/
unzip $CONFIGURATION
rm $CONFIGURATION
cd ../

## keeping configurations in resources so that they are included in the jar
cd src/main/resources/Configurations
unzip $CONFIGURATION 
rm $CONFIGURATION 
cd ../../../..

mvn $MVNOPTS -Dmaven.test.skip=true clean assembly:assembly

# Now package up the tools

zip --exclude .DS_STORE -r target/ISAcreator-$VERSION-all.zip Configurations Data "isatab files"
cd target
mv ISAcreator-$VERSION-jar-with-dependencies.jar ISAcreator.jar
zip -u ISAcreator-$VERSION-all.zip ISAcreator.jar

python ../bundler.py

echo "Packaging completed successfully!"
cd ../
zip --exclude .DS_STORE -r target/ISAcreator-$VERSION-all.zip target/ISAcreator.app Configurations Data "isatab files"

