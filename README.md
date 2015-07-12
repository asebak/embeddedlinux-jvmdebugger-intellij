![alt text][overviewlogo]
=============================
[![Build Status](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij.svg?branch=master)](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij)

```
https://plugins.jetbrains.com/plugin/7738
```

![alt text][logo]

Embedded Linux JVM Debugger is a Plugin for IntelliJ that makes it easier to develop Embedded Java applications for embedded systems running on Embedded Linux or on the Yocto Kernel.  It easily integrates with platforms like raspberry pi, ARM microprocessor boards, intel x86 boards, etc.

  - Supports Java 6+ with VM arguments and program paramaters
  - Custom Run and Debug configurations for your hardware board.
  - Automatic rapid deployment to your target device using SSH and SFTP using a delta algorithm for your external jars.
  - Project Templates and integration with PI4J https://github.com/Pi4J/pi4j/ specifically for the Raspberry Pi (RPi).
  - Supports Yocto Project (You need the Receipe For Java and the Board Support Package Implementation for your H/W board).


### How To Use

- Add a new Run Configuration : 

![alt text][config]

- Enter in required configurations (username, password, hostname) note: Enable sudo if you require GPIO access. Debug port can be anything that's not reserved.

- Run it on your Hardware running Embedded Linux.

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

[overviewlogo]: https://raw.githubusercontent.com/asebak/raspberrypi-intellij/master/resources/documentation/embeddedlinuxjvm.png
"Overview"

[logo]: https://raw.githubusercontent.com/asebak/raspberrypi-intellij/master/resources/documentation/sample1.png
"Sample Build Output"

[config]: https://raw.githubusercontent.com/asebak/raspberrypi-intellij/master/resources/documentation/sample2.png
"Sample Run Configuration"
