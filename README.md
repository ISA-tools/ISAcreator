#### ISAcreator is a Java desktop application which allows for creation and editing of ISATab files.

<hr>
<p align="center">
<i>
ISAcreator is the <a href="http://isa-tools.org/team">ISAtools</a> Java-based desktop editor for producing ISA-Tab files. 
</i>
</p>

<p align="center">
<i>
For programmatic management of ISA metadata for experimental descriptions (ISA-Tab and ISA-json representations), please check the <a href="https://github.com/ISA-tools/isa-api">ISA-API</a>, which is a Python-based API to parse, create, convert ISA metadata among other functionality.
</i>
</p>
<hr>

<p align="center">
<img src="http://isatools.files.wordpress.com/2011/09/isacreator1.png" align="center" alt="ISAcreator"/>
</p>

- General info: <http://isa-tools.org>
- Tools' overview in this short paper: <http://bioinformatics.oxfordjournals.org/content/26/18/2354.full.pdf+html>
- Issue tracking and bug reporting: <https://github.com/ISA-tools/ISAcreator/issues>
- Mainline source code: <https://github.com/ISA-tools/ISAcreator>
- Releases: <https://github.com/ISA-tools/ISAcreator/releases>
- Twitter: [@isatools](http://twitter.com/isatools)
- IRC: [irc://irc.freenode.net/#isatab](irc://irc.freenode.net/#isatab)
- [Development blog](http://isatools.wordpress.com) 

## Development

[![Build Status](https://travis-ci.org/ISA-tools/ISAcreator.svg?branch=master)](https://travis-ci.org/ISA-tools/ISAcreator)  [![Coverage Status](https://coveralls.io/repos/github/ISA-tools/ISAcreator/badge.svg?branch=development)](https://coveralls.io/github/ISA-tools/ISAcreator?branch=development)

### Running ISAcreator

To run ISAcreator locally:

1. Clone the code to your machine. You may clone from the primary repository at ISA-tools/ISAcreator, or from your own fork.
2. Compile the code (`./compile.sh`). This script runs the maven command (`mvn assembly:assembly -Dmaven.test.skip=true -Pbuild`) after downloading the required configuration (without which the tests will fail) and example ISAtab files. The build profile automatically sets some system variables, such as version, from information held within the pom.
3. Run the code (`java -cp target/ISAcreator-<version number>.jar org.isatools.isacreator.launch.ISAcreatorApplication`)

### Contributing

You should read this article about Github Flow: <http://scottchacon.com/2011/08/31/github-flow.html>. Although we don't strictly use Github flow, it's a really useful tutorial on how to use Git for collaborative development.

Ensure you have maven 2.2.1 installed and enabled as well as git. If you have trouble with dependencies, and you are running behind a proxy, please ensure you set the proxy in both MAVEN_OPTS and settings.xml. See [here](https://answers.atlassian.com/questions/31384/plugin-sdk-proxy-setting-for-https-is-not-working-but-http-is) for more information.

1. Fork it.
2. Clone your forked repository to your machine
3. Create a branch off of the development branch (`git checkout -b myisacreator`)
4. Make and test your changes
5. Run the tests (`mvn clean test`)
6. Commit your changes (`git commit -am "Added something useful"`)
7. Push to the branch (`git push origin myisacreator`)
8. Create a [Pull Request](http://help.github.com/pull-requests/) from your branch.
9. Promote it. Get others to drop in and +1 it.


### Refreshing your code against the master repository.

A simple `git pull git@github.com:ISA-tools/ISAcreator.git`

### Building using Vagrant

Instead of using your native OS environment, we have provided a Vagrant configuration in which you can build ISAcreator inside an Ubuntu VM. For instructions on installing and using Vagrant, see https://www.vagrantup.com/docs/installation/

Once you have Vagrant installed, just open a terminal window to the root of the ISAcreator project, and type `vagrant up`. After a log of log messages, eventually it should finish with a success message.

Then do `vagrant ssh` to log into the Ubuntu VM. From here do `cd /vagrant/` which maps to your project root. 

Then run `./Vagrant-bootstrap.sh` to install Java and Maven build tools. If all went well, your build environment should be ready to use `./compile.sh` and `./package.sh`.

Note: this builds using OpenJDK Java 1.7 and Maven 3.

#### Contributor License Agreement

Before we can accept any contributions to ISAcreator, you need to sign a [CLA](http://en.wikipedia.org/wiki/Contributor_License_Agreement):

Please email us at <isatools@googlegroups.com> to receive the CLA. Then you should sign this and send it back asap so we can add you to our development pool.

> The purpose of this agreement is to clearly define the terms under which intellectual property has been contributed to ISAcreator and thereby allow us to defend the project should there be a legal dispute regarding the software at some future time.

For a list of contributors, please see <http://github.com/ISA-tools/ISAcreator/contributors>

## License

CPAL License, available at <https://raw.githubusercontent.com/ISA-tools/ISAcreator/master/LICENSE.txt>
