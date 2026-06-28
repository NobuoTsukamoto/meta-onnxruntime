SUMMARY = "Hugging Face CLI - Command line interface for Hugging Face Hub."
DESCRIPTION = "Provides a command-line interface to interact with the Hugging Face Hub."
HOMEPAGE = "https://huggingface.co/docs/huggingface_hub/index"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "a44f222cd8f2f7c2eade30b5e7a04cac984a3235fa61ea87a0a5a31db77d561f"

PYPI_PACKAGE = "huggingface_hub"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-tqdm \
    python3-pyyaml \
    python3-filelock \
    python3-fsspec \
    python3-click \
    python3-typer \
    python3-httpx \
    python3-packaging \
    python3-typing-extensions \
"
