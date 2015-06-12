Embedded Linux JVM Debugger
=============================
[![Build Status](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij.svg?branch=master)](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij)

```
https://plugins.jetbrains.com/plugin/7738
```

![alt text][logo]

Embedded Linux JVM Debugger is a Plugin for IDEA that makes it easier to develop Java apps for embedded systems running on Embedded Linux.

  - Custom Run and Debug configurations
  - Automatic Deployments to your target device using SSH and SFTP.
  - Project Templates and integration with PI4J https://github.com/Pi4J/pi4j/ specifically for the Raspberry Pi (RPi)
  - Supports the Yocto Kernel (You need the Meta Board Support Package Receipe For Java)


### How To Use

- Add a new Run Configuration : 

![alt text][config]

- Enter in required configurations (username, password, hostname) note: Enable sudo if you require GPIO access. Debug port can be anything that's not reserved.

- Run it on your Hardware running Embedded Linux

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

[logo]: https://raw.githubusercontent.com/asebak/raspberrypi-intellij/master/sample1.png
"Sample Build Output"

[config]: https://raw.githubusercontent.com/asebak/raspberrypi-intellij/master/sample2.png
"Sample Run Configuration"
