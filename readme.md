[![Build Status](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?branch=master)](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper)

amv-highmobility-cryptotool-wrapper
========

# Development
## Build
```
$ ./gradlew clean build
```

## Usage
```
Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.builder()
   .pathToExecutable(BinaryHelper.getCryptotoolBinary())
   .workingDirectory(Files.createTempDir())
   .build());
   
// ...
```