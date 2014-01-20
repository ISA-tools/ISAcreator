#### ISAcreator is a Java desktop application which allows for creation and editing of ISATab files.

<p align="center">
<img src="http://isatools.files.wordpress.com/2011/09/isacreator1.png" align="center" alt="ISAcreator"/>
</p>

- General info: <http://isa-tools.org>
- Tools' overview in this short paper: <http://bioinformatics.oxfordjournals.org/content/26/18/2354.full.pdf+html>
- Issue tracking and bug reporting: <https://github.com/ISA-tools/ISAcreator/issues>
- Mainline source code: <https://github.com/ISA-tools/ISAcreator>
- Twitter: [@isatools](http://twitter.com/isatools)
- IRC: [irc://irc.freenode.net/#isatab](irc://irc.freenode.net/#isatab)
- [Development blog](http://isatools.wordpress.com) 

## Development

<a href="http://www.jetbrains.com/idea/" style="width:88px; height:31px;"><span style="margin: 0;padding: 0;position: absolute;top: -1px;left: 4px;font-size: 10px;cursor:pointer;"></span><img src="http://www.jetbrains.com/img/logos/recommend_idea1.gif" alt="The best Java IDE" border="0"/></a>

### Running ISAcreator

To run ISAcreator locally:

1. Clone the code to your machine. You may clone from the primary repository at ISA-tools/ISAcreator, or from your own fork.
2. Compile the code (`mvn assembly:assembly -Dmaven.test.skip=true -Pbuild`) - the build profile automatically sets some system variables like version etc. from information held within the pom.
3. Run the code (`java -cp target/ISAcreator-<version number>-jar-with-dependencies.jar org.isatools.isacreator.launch.ISAcreatorApplication`)

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


#### Contributor License Agreement

Before we can accept any contributions to ISAcreator, you need to sign a [CLA](http://en.wikipedia.org/wiki/Contributor_License_Agreement):

Please email us at <isatools@googlegroups.com> to receive the CLA. Then you should sign this and send it back asap so we can add you to our development pool.

> The purpose of this agreement is to clearly define the terms under which intellectual property has been contributed to ISAcreator and thereby allow us to defend the project should there be a legal dispute regarding the software at some future time.

For a list of contributors, please see <http://github.com/ISA-tools/ISAcreator/contributors>

## License

CPAL License, available at <http://isatab.sourceforge.net/licenses/ISAcreator-license.html>
