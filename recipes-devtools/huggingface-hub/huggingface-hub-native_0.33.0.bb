SUMMARY = "Hugging Face CLI - Command line interface for Hugging Face Hub (native)"
DESCRIPTION = "Provides a command-line interface to interact with the Hugging Face Hub."
HOMEPAGE = "https://huggingface.co/docs/huggingface_hub/index"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "aa31f70d29439d00ff7a33837c03f1f9dd83971ce4e29ad664d63ffb17d3bb97"

PYPI_PACKAGE = "huggingface_hub"

inherit pypi setuptools3 native

RDEPENDS:${PN} = " \
    python3-requests-native \
    python3-tqdm-native \
    python3-pyyaml-native \
    python3-filelock-native \
"