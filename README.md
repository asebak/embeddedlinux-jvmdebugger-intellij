![alt text][overviewlogo]
=============================
[![Build Status](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij.svg?branch=master)](https://travis-ci.org/asebak/embeddedlinux-jvmdebugger-intellij)

```
https://plugins.jetbrains.com/plugin/7738
```

![alt text][logo]

Embedded Linux JVM Debugger is a Plugin for IntelliJ IDEA that makes it easier to develop Embedded Java applications for embedded systems running on any variant of embedded linux.  It easily integrates any embedded system such as the raspberry pi or beaglebone black. As long as java can run on the embedded device and ssh protocol is enabled than it's never been simpler to develop embedded java applications.

  - Supports Java 6+ with Virtual Machine and Program Arguments.
  - Supports Multi-Module projects in IntelliJ.
  - Custom Run and Debug configurations for your hardware board.
  - Works cross platforms on the host machine whether that is Windows, Linux or Mac platforms.
  - Smart and rapid deployment algorithm deploy only changed files on the remote device.
  - Project Templates and integration with PI4J https://github.com/Pi4J/pi4j/ specifically for the Raspberry Pi I, II, or III.
  - Easily integratable with various kernels: Debian, Yocto Project, etc.


### How To Use

- Add a new Run Configuration : 

![alt text][config]

- Enter in required configurations (username, password or ssh private key file, hostname) note: Enable sudo if you require GPIO access. Debug port can be anything that's not reserved.

- Run it on your Hardware running Embedded Linux.

### Checking out and Building

Configure your Intellij environment using the following link: https://www.jetbrains.com/idea/help/configuring-intellij-platform-plugin-sdk.html

You can build the project directly from IDEA.  Continous Integration script can be executed using:
```sh
$ sudo sh ./ci-build.sh
```

### External Libraries
* Lombok Project (Install the IntelliJ plugin or project won't compile)
* Mockito/PowerMockito/JUnit
* Jsch
* Commons Compress
* IntelliJ SDK 2016 + Java 8 (since release 1.20)

### Development

If you want to contribute and add functionality, make sure to add unit tests if they are needed.

[overviewlogo]: https://raw.githubusercontent.com/asebak/embeddedlinux-jvmdebugger-intellij/master/Resources/documentation/embeddedlinuxjvm.png
"Overview"

[logo]: https://raw.githubusercontent.com/asebak/embeddedlinux-jvmdebugger-intellij/master/Resources/documentation/sample1.png
"Sample Build Output"

[config]: https://raw.githubusercontent.com/asebak/embeddedlinux-jvmdebugger-intellij/master/Resources/documentation/sample2.png
"Sample Run Configuration"
