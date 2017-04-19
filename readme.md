[![Build Status](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?branch=master)](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper)

amv-highmobility-cryptotool-wrapper
========

# build
```
./gradlew clean build
```

## release to bintray
```
./gradlew clean build -Prelease -PbintrayUser=${username} -PbintrayApiKey=${apiKey} bintrayUpload
```

# usage
```
Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.builder()
   .pathToExecutable(BinaryHelper.getCryptotoolBinary())
   .workingDirectory(Files.createTempDir())
   .build());
   
// ...
```