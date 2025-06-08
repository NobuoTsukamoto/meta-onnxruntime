SUMMARY = "ONNX Runtime GenAI recipe"
HOMEPAGE = "https://onnxruntime.ai/docs/genai/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d4a904ca135bb7bc912156fee12726f0"

# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV = "fea4e960e577876ebb9b73b1cd49213275bbbcbb"

SRC_URI = " \
    git://github.com/microsoft/onnxruntime-genai;branch=rel-0.8.2;protocol=https \
    file://0001-set-ORT_HEADER_DIR-genai.patch \
    file://0001-update-cxx-standard-23.patch \
    file://0001-Fix-ambiguous-cpu_span-constructor-call.patch \
"

S = "${WORKDIR}/git"

DEPENDS += "\
    python3-pip-native \
    python3-wheel-native \
    nlohmann-json \
    onnxruntime \
    python3 \
    python3-pybind11 \
"

inherit cmake python3-dir

OECMAKE_SOURCEPATH = "${S}"

ONNXRUNTIME_BUILD_DIR = "${WORKDIR}/build/"

EXTRA_OECMAKE:append = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
    -DUSE_CUDA=OFF \
    -DUSE_ROCM=OFF \
    -DUSE_DML=OFF \
    -DENABLE_JAVA=OFF \
    -DBUILD_WHEEL=ON \
    -DUSE_GUIDANCE=OFF \
    -DENABLE_TESTS=OFF \
    -DORT_HOME=${RECIPE_SYSROOT}/usr \
    -DPYTHON_EXECUTABLE=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
    -DPython_EXECUTABLE=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -DPYTHON_MODULE_EXTENSION=.cpython-${@d.getVar('PYTHON_PN').replace('python', '').replace('.', '')}${@d.getVar('PYTHON_PN').replace('python', '').replace('.', '')}-${@d.getVar('TARGET_ARCH').replace('aarch64', 'aarch64-linux-gnu')}.so \
 "

do_configure[network] = "1"

do_install:append() {
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}

    TAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip install --disable-pip-version-check -v \
    -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps wheel/onnxruntime_genai-${DPV}-*.whl

    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/libonnxruntime-genai.so
}

FILES:${PN} += " \
    /usr/README.md \
    /usr/ThirdPartyNotices.txt \
    /usr/SECURITY.md \
    /usr/LICENSE \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai-${DPV}.dist-info/* \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/*.py \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/LICENSE \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/ThirdPartyNotices.txt \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/models \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/__pycache__ \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/*.so \
    ${libdir}/libonnxruntime-genai.so \
"

FILES:${PN}-dev = " \
    ${includedir}/*.h \
"


INSANE_SKIP:${PN} += "buildpaths already-stripped"
INSANE_SKIP:${PN}-dev += "dev-elf"
