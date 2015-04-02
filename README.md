##Raspberry PI Intellij [![Build Status](https://travis-ci.org/asebak/raspberrypi-intellij.svg?branch=master)](https://travis-ci.org/asebak/raspberrypi-intellij)
========================================

Raspberry PI Intellij is a Plugin for IDEA that makes it easier to develop Java apps for the PI Platform.

  - Custom Run and Debug configurations
  - Automatic Deployments to your PI using SSH and SFTP.
  - Project Templates and integration with PI4J https://github.com/Pi4J/pi4j/


### Checking out and Building

Configure your Intellij environment using the following link: https://www.jetbrains.com/idea/help/configuring-intellij-platform-plugin-sdk.html

You can build the project directly from IDEA.  Continous Integration script can be executed using:
```sh
$ sudo sh ./ci-build.sh
```

### External Libraries
* Lombok Project
* SSHJ
* Mockito/PowerMockito/JUnit

### Development

If you want to contribute and add functionality, make sure to add unit tests if they are needed.



