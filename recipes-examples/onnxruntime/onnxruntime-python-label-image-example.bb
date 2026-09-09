DESCRIPTION = "Run the ONNX Runtime session creation and inference API"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e7edd1136ca484dbfa5dec5cbd785460"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_onnx-examples = "b1e355a70dcfcaeb426dafb88edc8b53a86c9874"

SRC_URI = " \
    git://github.com/NobuoTsukamoto/onnx-examples;name=onnx-examples;branch=main;protocol=https \
    https://s3.amazonaws.com/model-server/inputs/kitten.jpg;name=image \
    https://s3.amazonaws.com/onnx-model-zoo/synset.txt;name=labels \
    https://github.com/onnx/models/raw/main/validated/vision/classification/mobilenet/model/mobilenetv2-10.onnx;name=model \
"

SRC_URI[image.sha256sum] = "d5f516d7cde858db080760927ea73b1d5bab21a38ca0d4a1aea5b0e6f884969c"
SRC_URI[labels.sha256sum] = "acf75ef0abe89694b19056e0796401068b459c457baa30335f240c7692857355"
SRC_URI[model.sha256sum] = "0e7c0aa4bc74650386fa1d2c84705753de7c2bdb21909ada5c59154bb429e092"

RDEPENDS:${PN} += " \
    onnxruntime \
    opencv \
"
do_install:append() {
    install -d ${D}${datadir}/onnxruntime/examples/python/
    install -d ${D}${datadir}/onnxruntime/examples/python/test_data
    install -m 644 ${S}/python/classification/label_image.py ${D}${datadir}/onnxruntime/examples/python/

    install -m 644 ${UNPACKDIR}/kitten.jpg ${D}${datadir}/onnxruntime/examples/python/test_data/
    install -m 644 ${UNPACKDIR}/synset.txt ${D}${datadir}/onnxruntime/examples/python/test_data/
    install -m 644 ${UNPACKDIR}/mobilenetv2-10.onnx ${D}${datadir}/onnxruntime/examples/python/test_data/
}

FILES:${PN} += "${datadir}/onnxruntime/examples/*"
