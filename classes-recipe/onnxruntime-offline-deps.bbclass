# ONNX Runtime downloads its third-party dependencies with CMake FetchContent
# during do_configure, which bypasses the bitbake fetcher: no mirrors, no DL_DIR
# caching, no pinned revisions, and a single transient GitHub error fails the build.
#
# Recipes inheriting this class list those dependencies in SRC_URI instead (see
# scripts/update-onnxruntime-deps.py, which generates conf/onnxruntime-deps-<PV>.inc
# and sets ORT_DEPS_GIT and ORT_DEPS_FILES) and the class points cmake/deps.txt at
# the fetched sources. CMake still downloads, checks the SHA1 and applies the
# per-dependency patches of ONNX Runtime itself; only the download is replaced with
# a file:// copy. FETCHCONTENT_SOURCE_DIR_* is not used because it makes CMake skip
# those patches.
#
# ORT_DEPS_GIT is a list of deps.txt names, checked out in UNPACKDIR/ort-deps/<name>.
# CMake needs an archive, so one is created from the checkout, and the SHA1 in
# deps.txt is updated to match.
#
# ORT_DEPS_FILES is a list of <deps.txt name>:<archive in UNPACKDIR>, used as is.
ORT_DEPS_GIT ?= ""
ORT_DEPS_FILES ?= ""

do_configure:prepend() {
    archivedir=${WORKDIR}/ort-deps-archives
    rm -rf $archivedir
    mkdir -p $archivedir

    for name in ${ORT_DEPS_GIT}; do
        archive=$archivedir/$name.zip
        git -C ${UNPACKDIR}/ort-deps/$name archive --format=zip --prefix=$name/ -o $archive HEAD
        sha1=$(sha1sum $archive | cut -d' ' -f1)
        sed -i -E "s|^$name;.*$|$name;file://$archive;$sha1|" ${S}/cmake/deps.txt
        grep -q "^$name;file://$archive;$sha1$" ${S}/cmake/deps.txt || \
            bbfatal "$name not found in ${S}/cmake/deps.txt"
    done

    for entry in ${ORT_DEPS_FILES}; do
        name="${entry%%:*}"
        file="${entry#*:}"
        sed -i -E "s|^$name;[^;]+;|$name;file://${UNPACKDIR}/$file;|" ${S}/cmake/deps.txt
        grep -q "^$name;file://${UNPACKDIR}/$file;" ${S}/cmake/deps.txt || \
            bbfatal "$name not found in ${S}/cmake/deps.txt"
    done
}
