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
  

mvn $MVNOPTS -Dmaven.test.skip=true clean assembly:assembly

rm -rf Configurations

mkdir Configurations
cd Configurations
wget https://github.com/downloads/ISA-tools/Configuration-Files/isaconfig-default_v2011-02-18.zip --no-check-certificate
unzip isaconfig-default_v2011-02-18.zip
rm isaconfig-default_v2011-02-18.zip
cd ../

# Now package up the tools

zip --exclude .DS_STORE -r target/ISAcreator-$VERSION-all.zip Configurations Data "isatab files"
cd target
mv ISAcreator-$VERSION-jar-with-dependencies.jar ISAcreator.jar
zip -u ISAcreator-$VERSION-all.zip ISAcreator.jar

python ../bundler.py

pwd

zip --exclude .DS_STORE -r ISAcreator-$VERSION-mac.zip ../Configurations ../Data ../"isatab files"
zip -u ISAcreator-$VERSION-mac.zip ISAcreator.app

echo "Packaging completed successfully!"

