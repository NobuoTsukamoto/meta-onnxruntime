# meta-onnxruntime

Yocto layer for ONNX Runtime.

## The official website is:
- [ONNX Runtime](https://onnxruntime.ai/)


## Available BSP

Please note that it is not official support.

| BSP | ONNX Runtime version | Build status |
| :-- | :------ | :----------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | v1.18.1 | [![Bitbake raspberrypi](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_rpi.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_rpi.yml) |
| [meta-riscv](https://github.com/riscv/meta-riscv)                | v1.18.1 | [![Bitbake riscv](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_riscv.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_riscv.yml) |

### matrix

| BSP | MACHINE | Build status |
| :-- | :------ | :----------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | raspberrypi<br>raspberrypi0<br>raspberrypi0-wifi<br> raspberrypi-cm | NG |
|  | raspberrypi2<br>raspberrypi3<br>raspberrypi4<br>raspberrypi-cm3 | NG |
|  | raspberrypi0-2w-64<br>raspberrypi3-64<br>raspberrypi4-64 | OK  |
|  | raspberrypi5 | OK |
| [meta-riscv](https://github.com/riscv/meta-riscv) | qemueriscv32 | NG |
|  | qemuriscv64 | OK |

## Available recipes
- Framework
    - onnxruntime  
      [ONNX Runtime](recipes-framework/onnxruntime/onnxruntime_1.18.1.bb)
- Examples
    - onnxruntime-python-label-image-example  
      [Python label image](recipes-examples/onnxruntime/onnxruntime-python-label-image-example.bb)

## How to

### Quick start for the Raspberry Pi AArch64 (core-image-weston)

```
# Clone repositories and oe-init-build-env
$ git clone git://git.yoctoproject.org/poky.git
$ git clone git://git.yoctoproject.org/meta-raspberrypi
$ git clone git://git.openembedded.org/meta-openembedded
$ git clone https://github.com/NobuoTsukamoto/meta-onnxruntime.git
$ source poky/oe-init-build-env build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-raspberrypi/
$ bitbake-layers add-layer ../meta-onnxruntime/

# Add the package to 'conf/auto.conf' file. 
MACHINE ?= "raspberrypi4-64"
IMAGE_INSTALL:append = " onnxruntime"

# Build
$ bitbake core-image-weston
```