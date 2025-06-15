SUMMARY = "ONNX Runtime GenAI model_qa.py example recipe"
HOMEPAGE = "https://onnxruntime.ai/docs/genai/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d4a904ca135bb7bc912156fee12726f0"

SRCREV = "fea4e960e577876ebb9b73b1cd49213275bbbcbb"

SRC_URI = " \
    git://github.com/microsoft/onnxruntime-genai;branch=rel-0.8.2;protocol=https \
"

DEPENDS = "\
    huggingface-hub-native \
"

RDEPENDS:${PN} = " \
    onnxruntime-genai \
"

S = "${WORKDIR}/git"

do_configure[network] = "1"

do_configure:append() {
    mkdir -p ${S}/downloads
    huggingface-cli download microsoft/Phi-4-mini-instruct-onnx --include cpu_and_mobile/cpu-int4-rtn-block-32-acc-level-4/* --local-dir ${S}/downloads
}

do_install:append() {
    install -d ${D}${datadir}/onnxruntime_genai
    install -d ${D}${datadir}/onnxruntime_genai/examples
    install -d ${D}${datadir}/onnxruntime_genai/examples/python
    install -m 644 ${S}/examples/python/model-qa.py ${D}${datadir}/onnxruntime_genai/examples/python/

    install -d ${D}${datadir}/onnxruntime_genai/examples/python/cpu_and_mobile/
    install -d ${D}${datadir}/onnxruntime_genai/examples/python/cpu_and_mobile/cpu-int4-rtn-block-32-acc-level-4
    install -m 644 ${S}/downloads/cpu_and_mobile/cpu-int4-rtn-block-32-acc-level-4/* ${D}${datadir}/onnxruntime_genai/examples/python/cpu_and_mobile/cpu-int4-rtn-block-32-acc-level-4/
}

FILES:${PN} += "${datadir}/onnxruntime_genai/examples/*"
