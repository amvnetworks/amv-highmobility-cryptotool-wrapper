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

## getting an instance
```java
Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.createDefault());
   
// ...
```

### instance initialized with issuer
A issuer is representing a key pair (`keys`) with an optional name (`name`).
With an `CryptotoolWithIssuer` instances you can omit the public and private keys when
creating device certificates and signing messages.
```java
CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.createDefault();

Cryptotool cryptotool = new CryptotoolImpl(cryptotoolOptions);

CertificateIssuer certificateIssuer = CertificateIssuerImpl.builder()
        .name(TestUtils.generateRandomIssuer())
        .keys(cryptotool.generateKeys().block())
        .build();

Cryptotool cryptotoolWithIssuer = new CryptotoolWithIssuerImpl(cryptotoolOptions, certificateIssuer);
// ...
```