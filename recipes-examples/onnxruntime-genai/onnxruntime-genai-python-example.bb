SUMMARY = "ONNX Runtime GenAI model_qa.py example recipe"
HOMEPAGE = "https://onnxruntime.ai/docs/genai/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d4a904ca135bb7bc912156fee12726f0"

SRCREV = "ed5f4e87147731e5b07810f9f5c90103b3603cdf"

SRC_URI = " \
    git://github.com/microsoft/onnxruntime-genai;branch=rel-0.15.2;protocol=https \
"

RDEPENDS:${PN} = " \
    onnxruntime-genai \
"

do_install:append() {
    install -d ${D}${datadir}/onnxruntime_genai
    install -d ${D}${datadir}/onnxruntime_genai/examples
    install -d ${D}${datadir}/onnxruntime_genai/examples/python
    install -m 644 ${S}/examples/python/model-qa.py ${D}${datadir}/onnxruntime_genai/examples/python/
}

FILES:${PN} += "${datadir}/onnxruntime_genai/examples/*"
