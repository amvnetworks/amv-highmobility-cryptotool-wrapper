[![Build Status](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?branch=master)](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper)
[![Download](https://api.bintray.com/packages/amvnetworks/amv-highmobility-cryptotool-wrapper/amv-highmobility-cryptotool-wrapper/images/download.svg)](https://bintray.com/amvnetworks/amv-highmobility-cryptotool-wrapper/amv-highmobility-cryptotool-wrapper/_latestVersion)
[![License](https://img.shields.io/github/license/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?maxAge=2592000)](https://github.com/amvnetworks/amv-highmobility-cryptotool-wrapper/blob/master/LICENSE)

amv-highmobility-cryptotool-wrapper
========

# build
```bash
./gradlew clean build
```

## release to bintray
```bash
./gradlew clean build -Prelease -PbintrayUser=${username} -PbintrayApiKey=${apiKey} bintrayUpload
```

## release to local repository
```bash
./gradlew clean build publishToMavenLocal
```

# usage
```java
Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.builder()
   .pathToExecutable(BinaryHelper.getCryptotoolBinary())
   .workingDirectory(Files.createTempDir())
   .build());
   
// ...
```