#!/bin/sh
# Use this file to build all the packages about ISA-Tools
# More details in the POM. 
#

#MVNOPTS="--offline"

VERSION=`xpath -e '/project/version/text()' pom.xml  2>/dev/null`

if [ "$VERSION" = "" ] ; then 
  echo "Couldn't extract version from pom.xml. Exiting."
fi
  

mvn $MVNOPTS -Dmaven.test.skip=true clean  assembly:assembly

zip --exclude .DS_STORE -r target/ISAcreator-$VERSION.zip Configurations Data "isatab files"
cd target
mv ISAcreator-$VERSION-jar-with-dependencies.jar ISAcreator.jar
zip -u ISAcreator-$VERSION.zip ISAcreator.jar 

cd ..
