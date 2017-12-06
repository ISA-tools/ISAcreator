#!/bin/sh

rm -rf Configurations

echo "Retrieving configuration from GitHub repo"
mkdir Configurations
cd Configurations
wget https://github.com/downloads/ISA-tools/Configuration-Files/isaconfig-default_v2011-02-18.zip --no-check-certificate
wget https://github.com/downloads/ISA-tools/Configuration-Files/isaconfig-default_v2011-02-18-MIMARKS-soil_v2011-02-18.zip --no-check-certificate
wget https://github.com/downloads/ISA-tools/Configuration-Files/isaconfig-default_v2011-02-18-MIMARKS-water_v2011-02-18.zip --no-check-certificate
wget https://github.com/downloads/ISA-tools/Configuration-Files/isaconfig-MISFISHIE-rodent-january-2011.zip --no-check-certificate

echo "Unzipping files"

unzip isaconfig-default_v2011-02-18.zip
unzip isaconfig-default_v2011-02-18-MIMARKS-soil_v2011-02-18.zip
unzip isaconfig-default_v2011-02-18-MIMARKS-water_v2011-02-18.zip
unzip isaconfig-MISFISHIE-rodent-january-2011.zip

echo "Cleaning up directory"
rm -rf __*
rm *.zip

cd ../

echo "Running tests"

# now run tests
mvn $MVNOPTS clean test jacoco:report coveralls:report

echo "Testing completed successfully!"

