SUMMARY = "Hugging Face CLI - Command line interface for Hugging Face Hub."
DESCRIPTION = "Provides a command-line interface to interact with the Hugging Face Hub."
HOMEPAGE = "https://huggingface.co/docs/huggingface_hub/index"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "a4228daa6fb001be3f4f4bdaf9a0db00e1739235702848df00885c9b5742c85c"

PYPI_PACKAGE = "huggingface_hub"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-requests \
    python3-tqdm \
    python3-pyyaml \
    python3-filelock \
"
