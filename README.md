# meta-onnxruntime

Yocto layer for ONNX Runtime.

## The official website is:
- [ONNX Runtime](https://onnxruntime.ai/)
- [ONNX Runtime generate API](https://onnxruntime.ai/docs/genai/)


## Available BSP

Please note that it is not official support.

| BSP | ONNX Runtime | ONNX Runtime generate API |Build status |
| :-- | :-- | :-- | :-- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | v1.30.0 | v0.17.0 |[![Bitbake raspberrypi](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_rpi.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_rpi.yml) |
| [meta-riscv](https://github.com/riscv/meta-riscv) | v1.30.0 | v0.17.0 | [![Bitbake riscv](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_riscv.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_riscv.yml) |
| [meta-intel](https://github.com/YoeDistro/meta-intel) | v1.30.0 | v0.17.0 |[![Bitbake intel](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_intel.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-onnxruntime/actions/workflows/build_intel.yml) |

### matrix

| BSP | MACHINE | Build status |
| :-- | :------ | :----------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | raspberrypi<br>raspberrypi0<br>raspberrypi0-wifi<br> raspberrypi-cm | NG |
|  | raspberrypi2<br>raspberrypi3<br>raspberrypi4<br>raspberrypi-cm3 | NG |
|  | raspberrypi0-2w-64<br>raspberrypi3-64<br>raspberrypi4-64 | OK  |
|  | raspberrypi5 | OK |
| [meta-riscv](https://github.com/riscv/meta-riscv) | qemueriscv32 | NG |
|  | qemuriscv64 | OK |
| [meta-intel](https://github.com/YoeDistro/meta-intel) | genericx86-64 | OK |
|  | intel-corei7-64 | OK |
|  |  intel-skylake-64 | OK |

## Available recipes
- Framework
    - onnxruntime  
      [ONNX Runtime](recipes-framework/onnxruntime/onnxruntime_1.30.0.bb)
    - onnxrintime-genai  
      [ONNX Runtime generate API](recipes-framework/onnxruntime-genai/onnxruntime-genai_0.17.0.bb)
- Examples
    - onnxruntime-python-label-image-example  
      [Python label image](recipes-examples/onnxruntime/onnxruntime-python-label-image-example.bb)
    - onnxruntime-genai-python-example  
      [ONNX Runtime GenAI model_qa.py example](recipes-examples/onnxruntime-genai/onnxruntime-genai-python-example.bb)

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

## Third-party dependencies of ONNX Runtime

ONNX Runtime downloads its third-party dependencies (abseil, protobuf, onnx, ...)
with CMake `FetchContent` while configuring. To avoid bypassing the bitbake
fetcher, the `onnxruntime` and `onnxruntime-test-all` recipes list these
dependencies in `SRC_URI` instead, so that they benefit from mirrors, `DL_DIR`
and pinned revisions, and `do_configure` does not need network access.

### How it works

- `conf/onnxruntime-deps-<version>.inc` is included by the recipes with
  `require conf/onnxruntime-deps-${PV}.inc`. It is generated, do not edit it.
  - Dependencies hosted in a GitHub repository are fetched with the git fetcher,
    pinned with `SRCREV_<name>`, and listed in `ORT_DEPS_GIT`.
  - Other dependencies (the protobuf sources and the `protoc` binaries) are fetched
    as files, pinned with `SRC_URI[<name>.sha256sum]`, and listed in `ORT_DEPS_FILES`.
- [`classes-recipe/onnxruntime-offline-deps.bbclass`](classes-recipe/onnxruntime-offline-deps.bbclass)
  rewrites `cmake/deps.txt` in `do_configure` so that each dependency is a
  `file://` URL of the fetched sources (for git dependencies, an archive created
  from the checkout and its SHA1). CMake therefore still downloads, checks and
  patches the dependencies like upstream does, but nothing is fetched from the
  network.

### Updating ONNX Runtime

When adding a recipe for a new ONNX Runtime version:

```
$ scripts/update-onnxruntime-deps.py <version>
```

This reads `cmake/deps.txt` of the `v<version>` release, resolves the git tags to
commits with `git ls-remote`, downloads the other files to check their SHA1 against
`deps.txt` and compute their SHA256, and writes `conf/onnxruntime-deps-<version>.inc`.
Then make the recipes `require conf/onnxruntime-deps-${PV}.inc` and
`inherit onnxruntime-offline-deps`. Network access is only needed to run the script.

The list of dependencies that the recipes need is `DEPS` in the script. It depends
on the build configuration (`EXTRA_OECMAKE`), not on the release. If `do_configure`
fails with a download error after an update, a new dependency is needed: add its
name from `cmake/deps.txt` to `DEPS` and run the script again.

The unit tests of the script do not need network access:

```
$ python3 -m unittest discover -s scripts
```
