SUMMARY = "ONNX Runtime recipe"
HOMEPAGE = "https://onnxruntime.ai/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0f7e3b1308cb5c00b372a6e78835732d"

# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_onnxruntime = "56b660f36940a919295e6f1e18ad3a9a93a10bf7"

SRC_URI = " \
    git://github.com/microsoft/onnxruntime.git;name=onnxruntime;branch=rel-1.17.3;protocol=https \
    file://0001-fix_requirements.txt.patch \
    file://0001-modify_platform_cpp.patch \
    file://0001-remove-onnxruntime_test.patch \
"

SRC_URI:append:raspberrypi3-64 = " \
    file://0001-fix_mlas_build_error.patch \
"

SRC_URI:append:raspberrypi4-64 = " \
    file://0001-fix_mlas_build_error.patch \
"

SRC_URI:append:raspberrypi5 = " \
    file://0001-fix_mlas_build_error_rpi5.patch \
"

SRC_URI:append:riscv32 = " \
    file://0001-fix_riscv_build_error.patch \
"

SRC_URI:append:riscv64 = " \
    file://0001-fix_riscv_build_error.patch \
"

S = "${WORKDIR}/git"

DEPENDS += "\
            python3-pip-native \
            python3-wheel-native \
            python3 \
            python3-numpy \
            python3-pybind11 \
"

RDEPENDS:${PN} += " \
    python3 \
    python3-numpy \
"

inherit cmake python3-dir

OECMAKE_SOURCEPATH = "${S}/cmake"

ONNXRUNTIME_BUILD_DIR = "${WORKDIR}/build/"

ONNXRUNTIME_TARGET_ARCH:raspberrypi = "armv6"
ONNXRUNTIME_TARGET_ARCH:raspberrypi0 = "armv6"
ONNXRUNTIME_TARGET_ARCH:raspberrypi0-wifi = "armv6"
ONNXRUNTIME_TARGET_ARCH:raspberrypi-cm = "armv6"
ONNXRUNTIME_TARGET_ARCH:raspberrypi2 = "armv7"
ONNXRUNTIME_TARGET_ARCH:raspberrypi3 = "armv7"
ONNXRUNTIME_TARGET_ARCH:raspberrypi4 = "armv7"
ONNXRUNTIME_TARGET_ARCH:raspberrypi-cm3 = "armv7"
ONNXRUNTIME_TARGET_ARCH:raspberrypi0-2w-64 = "aarch64"
ONNXRUNTIME_TARGET_ARCH:raspberrypi3-64 = "aarch64"
ONNXRUNTIME_TARGET_ARCH:raspberrypi4-64 = "aarch64"
ONNXRUNTIME_TARGET_ARCH:raspberrypi5 = "aarch64"
ONNXRUNTIME_TARGET_ARCH:riscv32 = "riscv32"
ONNXRUNTIME_TARGET_ARCH:riscv64 = "riscv64"

PYBIND11_INCLUDE = "${PKG_CONFIG_SYSROOT_DIR}/${PYTHON_SITEPACKAGES_DIR}/pybind11/pybind11/include"
NUMPY_INCLUDE = "${PKG_CONFIG_SYSROOT_DIR}/${PYTHON_SITEPACKAGES_DIR}/numpy/core/include"

OECMAKE_C_FLAGS += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_INCLUDE} -I${NUMPY_INCLUDE}"
OECMAKE_C_FLAGS_RELEASE += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_INCLUDE} -I${NUMPY_INCLUDE}"
OECMAKE_CXX_FLAGS += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_INCLUDE} -I${NUMPY_INCLUDE}"
OECMAKE_CXX_FLAGS_RELEASE += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_INCLUDE} -I${NUMPY_INCLUDE}"

EXTRA_OECMAKE:append = " \
    -Donnxruntime_RUN_ONNX_TESTS=OFF \
    -Donnxruntime_GENERATE_TEST_REPORTS=ON \
    -Donnxruntime_USE_MIMALLOC=OFF \
    -Donnxruntime_ENABLE_PYTHON=ON \
    -DPython_EXECUTABLE=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
    -DPYTHON_EXECUTABLE=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
    -Donnxruntime_BUILD_CSHARP=OFF \
    -Donnxruntime_BUILD_JAVA=OFF \
    -Donnxruntime_BUILD_NODEJS=OFF \
    -Donnxruntime_BUILD_OBJC=OFF \
    -Donnxruntime_BUILD_SHARED_LIB=ON \
    -Donnxruntime_BUILD_APPLE_FRAMEWORK=OFF \
    -Donnxruntime_USE_DNNL=OFF \
    -Donnxruntime_USE_NNAPI_BUILTIN=OFF \
    -Donnxruntime_USE_RKNPU=OFF \
    -Donnxruntime_USE_LLVM=OFF \
    -Donnxruntime_ENABLE_MICROSOFT_INTERNAL=OFF \
    -Donnxruntime_USE_VITISAI=OFF \
    -Donnxruntime_USE_TENSORRT=OFF \
    -Donnxruntime_USE_TENSORRT_BUILTIN_PARSER=ON \
    -Donnxruntime_USE_TVM=OFF \
    -Donnxruntime_TVM_CUDA_RUNTIME=OFF \
    -Donnxruntime_TVM_USE_HASH=OFF \
    -Donnxruntime_USE_MIGRAPHX=OFF \
    -Donnxruntime_DISABLE_CONTRIB_OPS=OFF \
    -Donnxruntime_DISABLE_ML_OPS=OFF \
    -Donnxruntime_DISABLE_RTTI=OFF \
    -Donnxruntime_DISABLE_EXCEPTIONS=OFF \
    -Donnxruntime_MINIMAL_BUILD=OFF \
    -Donnxruntime_EXTENDED_MINIMAL_BUILD=OFF \
    -Donnxruntime_MINIMAL_BUILD_CUSTOM_OPS=OFF \
    -Donnxruntime_REDUCED_OPS_BUILD=OFF \
    -Donnxruntime_ENABLE_LANGUAGE_INTEROP_OPS=OFF \
    -Donnxruntime_USE_DML=OFF \
    -Donnxruntime_USE_WINML=OFF \
    -Donnxruntime_BUILD_MS_EXPERIMENTAL_OPS=OFF \
    -Donnxruntime_USE_TELEMETRY=OFF \
    -Donnxruntime_ENABLE_LTO=OFF \
    -Donnxruntime_USE_ACL=OFF \
    -Donnxruntime_USE_ACL_1902=OFF \
    -Donnxruntime_USE_ACL_1905=OFF \
    -Donnxruntime_USE_ACL_1908=OFF \
    -Donnxruntime_USE_ACL_2002=OFF \
    -Donnxruntime_USE_ARMNN=OFF \
    -Donnxruntime_ARMNN_RELU_USE_CPU=ON \
    -Donnxruntime_ARMNN_BN_USE_CPU=ON \
    -Donnxruntime_USE_JSEP=OFF \
    -Donnxruntime_ENABLE_NVTX_PROFILE=OFF \
    -Donnxruntime_ENABLE_TRAINING=OFF \
    -Donnxruntime_ENABLE_TRAINING_OPS=OFF \
    -Donnxruntime_ENABLE_TRAINING_APIS=OFF \
    -Donnxruntime_ENABLE_CPU_FP16_OPS=OFF \
    -Donnxruntime_USE_NCCL=OFF \
    -Donnxruntime_BUILD_BENCHMARKS=OFF \
    -Donnxruntime_USE_ROCM=OFF \
    -DOnnxruntime_GCOV_COVERAGE=OFF \
    -Donnxruntime_USE_MPI=OFF \
    -Donnxruntime_ENABLE_MEMORY_PROFILE=OFF \
    -Donnxruntime_ENABLE_CUDA_LINE_NUMBER_INFO=OFF \
    -Donnxruntime_BUILD_WEBASSEMBLY_STATIC_LIB=OFF \
    -Donnxruntime_ENABLE_WEBASSEMBLY_EXCEPTION_CATCHING=ON \
    -Donnxruntime_ENABLE_WEBASSEMBLY_API_EXCEPTION_CATCHING=OFF \
    -Donnxruntime_ENABLE_WEBASSEMBLY_EXCEPTION_THROWING=ON \
    -Donnxruntime_WEBASSEMBLY_RUN_TESTS_IN_BROWSER=OFF \
    -Donnxruntime_ENABLE_WEBASSEMBLY_THREADS=OFF \
    -Donnxruntime_ENABLE_WEBASSEMBLY_DEBUG_INFO=OFF \
    -Donnxruntime_ENABLE_WEBASSEMBLY_PROFILING=OFF \
    -Donnxruntime_ENABLE_LAZY_TENSOR=OFF \
    -Donnxruntime_ENABLE_EXTERNAL_CUSTOM_OP_SCHEMAS=OFF \
    -Donnxruntime_ENABLE_CUDA_PROFILING=OFF \
    -Donnxruntime_ENABLE_ROCM_PROFILING=OFF \
    -Donnxruntime_USE_WEBNN=OFF \
    -Donnxruntime_USE_CANN=OFF \
    -Donnxruntime_USE_TRITON_KERNEL=OFF \
    -Donnxruntime_DISABLE_FLOAT8_TYPES=OFF \
    -DCMAKE_INSTALL_PREFIX=/usr  \
    -DCMAKE_CXX_FLAGS=-Wno-error=maybe-uninitialize \
    -DCMAKE_CXX_FLAGS=-Wno-error=array-bounds \
    -DCMAKE_TLS_VERIFY=ON -DFETCHCONTENT_QUIET=OFF \
    -Donnxruntime_ENABLE_MEMLEAK_CHECKER=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_PREFIX_PATH=${WORKDIR}/git/build/Linux/Release/installed \
    -DCMAKE_SYSTEM_PROCESSOR=${ONNXRUNTIME_TARGET_ARCH} \
    -Donnxruntime_target_platform=ARM \
    -DMLAS_SOURCE_IS_NOT_SET=OFF \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -Donnxruntime_BUILD_UNIT_TESTS=OFF \
"

EXTRA_OECMAKE:append:raspberrypi5 = " \
    -Donnxruntime_USE_XNNPACK=OFF \
"

EXTRA_OECMAKE:append:raspberrypi4-64 = " \
    -Donnxruntime_USE_XNNPACK=OFF \
"

CMAKE_VERBOSE = "VERBOSE=1"

do_configure[network] = "1"

do_compile:append() {
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/setup.py bdist_wheel
}

do_install:append() {
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}

    TAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip install --disable-pip-version-check -v \
    -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps dist/onnxruntime-${DPV}-*.whl
}

FILES:${PN}-dev = " \
    ${includedir}/onnxruntime/*.h \
    ${libdir}/libonnxruntime.so \
    ${libdir}/pkgconfig/libonnxruntime.pc \
    ${libdir}/cmake/onnxruntime/*.cmake \
"

FILES:${PN} += "${libdir}/libonnxruntime.so"
FILES:${PN} += "${libdir}/libonnxruntime.so.*"
FILES:${PN} += "${libdir}/libonnxruntime_providers_shared.so"
FILES:${PN} += "${libdir}/python3.*/site-packages/*"
FILES:${PN} += "${bindir}/onnx_test_runner"
