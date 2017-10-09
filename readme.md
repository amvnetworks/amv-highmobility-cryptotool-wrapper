[![Build Status](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?branch=master)](https://travis-ci.org/amvnetworks/amv-highmobility-cryptotool-wrapper)
[![Download](https://api.bintray.com/packages/amvnetworks/amv-highmobility-cryptotool-wrapper/amv-highmobility-cryptotool-wrapper/images/download.svg)](https://bintray.com/amvnetworks/amv-highmobility-cryptotool-wrapper/amv-highmobility-cryptotool-wrapper/_latestVersion)
[![License](https://img.shields.io/github/license/amvnetworks/amv-highmobility-cryptotool-wrapper.svg?maxAge=2592000)](https://github.com/amvnetworks/amv-highmobility-cryptotool-wrapper/blob/master/LICENSE)

amv-highmobility-cryptotool-wrapper
========
amv-highmobility-cryptotool-wrapper requires Java version 1.8.0_92 or greater.


# build
```bash
./gradlew clean build
```

# development

## create a release
```bash
./gradlew final -Prelease.scope=patch 
```

## release to bintray
```bash
./gradlew clean build bintrayUpload
    -Prelease.stage=final
    -Prelease.scope=patch
    -PreleaseToBintray
    -PbintrayUser=${username}
    -PbintrayApiKey=${apiKey} 
```

## release to local repository
```bash
./gradlew clean build publishToMavenLocal
```

## ide
this library uses lombok. enable annotation processing in your ide.
```bash
./gradlew installLombok
```

# requirements
make sure you have installed libssl.so.1.0.0.
e.g. for [debian (x86_64)](https://packages.debian.org/wheezy/amd64/libssl1.0.0/download)

# usage

## getting an instance
```java
Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.createDefault());
   
// ...
```

### instance initialized with issuer
A issuer is representing a key pair (`keys`) with an optional name (`name`).
When signing messages or creating device certificates with an instance of
`CryptotoolWithIssuer` the public and private key params can be omitted.
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

## commands

### keys
```java
cryptotool.generateKeys()
    .subscribe(keys -> {
        log.info("public key: {}", keys.getPublicKey());
        log.info("private key: {}", keys.getPublicKey());
    });
```

### generate signature
```java
String myMessage = ...

cryptotoolWithIssuer.generateSignature(myMessage)
    .subscribe(signature -> {
        log.info("signature: {}", signature.getSignature());
    });
```

### verify signature
```java
String myMessage = ...
String signature =  ...

cryptotoolWithIssuer.verifySignature(myMessage, signature)
    .subscribe(validity -> {
        log.info("signature validity: {}", validity);
    });
```

### create device certificate
```java
String appId = ...
String serial = ...

cryptotoolWithIssuer.createDeviceCertificate(appId, serial)
    .subscribe(deviceCertificate -> {
        log.info("device certificate: {}", deviceCertificate);
    });
```

### create access certificate
```java
String gainingSerial = ...;
String publicKey = ...;
String providingSerial = ...;
LocalDateTime startDate = LocalDateTime.now();
LocalDateTime endDate = startDate.plusDays(1);

cryptotoolWithIssuer.createAccessCertificate(gainingSerial, publicKey, providingSerial, startDate, endDate)
    .subscribe(accessCertificate -> {
        log.info("access certificate: {}", accessCertificate);
    });
```

# license
The project is licensed under the MIT license. See
[LICENSE](LICENSE) for details.
