SUMMARY = "Hugging Face CLI - Command line interface for Hugging Face Hub."
DESCRIPTION = "Provides a command-line interface to interact with the Hugging Face Hub."
HOMEPAGE = "https://huggingface.co/docs/huggingface_hub/index"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "87b506d5b45f0d1af58df7cf8bab993ded25d6077c2e959af58444df8b9589f3"

PYPI_PACKAGE = "huggingface_hub"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-requests \
    python3-tqdm \
    python3-pyyaml \
    python3-filelock \
    python3-click \
    python3-typer \
    python3-httpx \
"
