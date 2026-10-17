#!/usr/bin/env python3
# Copyright (c) 2026, Snap Inc.
# SPDX-License-Identifier: MIT

"""Unit tests for update-onnxruntime-deps.py, they do not need network access.

Usage: python3 -m unittest discover -s scripts
"""

import hashlib
import importlib.util
import os
import subprocess
import sys
import tempfile
import unittest
from unittest import mock

# The script has dashes in its name, so it cannot be imported with "import".
_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "update-onnxruntime-deps.py")
_spec = importlib.util.spec_from_file_location("update_onnxruntime_deps", _path)
u = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(u)

COMMIT = "0123456789abcdef0123456789abcdef01234567"
TAG_COMMIT = "89abcdef0123456789abcdef0123456789abcdef"


def ls_remote(*lines):
    """Output of git ls-remote as returned by run()."""
    return "".join(line + "\n" for line in lines).encode()


class ParseDepsTxt(unittest.TestCase):
    def test_parse(self):
        text = (
            "# comment\n"
            "\n"
            "abseil_cpp;https://github.com/abseil/abseil-cpp/archive/refs/tags/20250814.0.zip;aaaa\n"
            "   \n"
            "protoc_linux_x64;https://example.com/protoc.zip;bbbb\n"
        )
        self.assertEqual(u.parse_deps_txt(text), {
            "abseil_cpp": ("https://github.com/abseil/abseil-cpp/archive/refs/tags/20250814.0.zip", "aaaa"),
            "protoc_linux_x64": ("https://example.com/protoc.zip", "bbbb"),
        })

    def test_malformed_line(self):
        with self.assertRaises(ValueError):
            u.parse_deps_txt("name;only-two-fields\n")


class ParseGithubArchive(unittest.TestCase):
    def test_tag_zip(self):
        url = "https://github.com/google/re2/archive/refs/tags/2024-07-02.zip"
        self.assertEqual(u.parse_github_archive(url), ("google/re2", "2024-07-02"))

    def test_tag_tar_gz(self):
        url = "https://github.com/google/re2/archive/refs/tags/2024-07-02.tar.gz"
        self.assertEqual(u.parse_github_archive(url), ("google/re2", "2024-07-02"))

    def test_commit(self):
        url = f"https://github.com/dmlc/dlpack/archive/{COMMIT}.zip"
        self.assertEqual(u.parse_github_archive(url), ("dmlc/dlpack", COMMIT))

    def test_commit_with_file_name(self):
        url = f"https://github.com/onnx/onnx/archive/{COMMIT}/onnx-{COMMIT}.zip"
        self.assertEqual(u.parse_github_archive(url), ("onnx/onnx", COMMIT))

    def test_tag_with_dots_is_kept(self):
        url = "https://github.com/pybind/pybind11/archive/refs/tags/v2.13.6.zip"
        self.assertEqual(u.parse_github_archive(url), ("pybind/pybind11", "v2.13.6"))

    def test_release_asset_is_not_an_archive(self):
        url = "https://github.com/protocolbuffers/protobuf/releases/download/v33.6/protobuf-33.6.tar.gz"
        self.assertIsNone(u.parse_github_archive(url))

    def test_other_host(self):
        self.assertIsNone(u.parse_github_archive("https://example.com/foo/archive/v1.zip"))


class CommitOf(unittest.TestCase):
    def test_commit_is_returned_without_network(self):
        with mock.patch.object(u, "run") as run:
            self.assertEqual(u.commit_of("a/b", COMMIT), COMMIT)
        run.assert_not_called()

    def test_lightweight_tag(self):
        out = ls_remote(f"{COMMIT}\trefs/tags/v1.0")
        with mock.patch.object(u, "run", return_value=out) as run:
            self.assertEqual(u.commit_of("a/b", "v1.0"), COMMIT)
        run.assert_called_once_with("git", "ls-remote", "https://github.com/a/b.git",
                                    "refs/tags/v1.0", "refs/tags/v1.0^{}")

    def test_annotated_tag_is_peeled(self):
        out = ls_remote(f"{COMMIT}\trefs/tags/v1.0", f"{TAG_COMMIT}\trefs/tags/v1.0^{{}}")
        with mock.patch.object(u, "run", return_value=out):
            self.assertEqual(u.commit_of("a/b", "v1.0"), TAG_COMMIT)

    def test_unknown_tag(self):
        with mock.patch.object(u, "run", return_value=b""):
            with self.assertRaises(KeyError):
                u.commit_of("a/b", "v1.0")

    def test_short_hash_is_resolved_as_a_tag(self):
        # Only a full 40 digit hash is taken as a commit.
        with mock.patch.object(u, "run", return_value=b"") as run:
            with self.assertRaises(KeyError):
                u.commit_of("a/b", "0123456")
        run.assert_called_once()


class CheckSha1(unittest.TestCase):
    DATA = b"some data"

    def test_match(self):
        u.check_sha1("https://example.com/f", self.DATA, hashlib.sha1(self.DATA).hexdigest())

    def test_mismatch(self):
        with self.assertRaises(SystemExit) as cm:
            u.check_sha1("https://example.com/f", self.DATA, "0" * 40)
        self.assertIn("https://example.com/f", str(cm.exception))
        self.assertIn("SHA1 differs", str(cm.exception))


class Run(unittest.TestCase):
    def test_retries_then_succeeds(self):
        error = subprocess.CalledProcessError(1, "x", stderr=b"HTTP 500")
        ok = subprocess.CompletedProcess("x", 0, stdout=b"output")
        with mock.patch.object(u.subprocess, "run", side_effect=[error, error, ok]) as sub, \
                mock.patch.object(u.time, "sleep") as sleep:
            self.assertEqual(u.run("x"), b"output")
        self.assertEqual(sub.call_count, 3)
        self.assertEqual([c.args[0] for c in sleep.call_args_list], [1, 2])

    def test_gives_up(self):
        error = subprocess.CalledProcessError(1, "x", stderr=b"HTTP 500")
        with mock.patch.object(u.subprocess, "run", side_effect=error) as sub, \
                mock.patch.object(u.time, "sleep"):
            with self.assertRaises(SystemExit) as cm:
                u.run("x", "y")
        self.assertEqual(sub.call_count, 5)
        self.assertIn("x y: HTTP 500", str(cm.exception))


class RenderInc(unittest.TestCase):
    GIT = [("re2", "google/re2", COMMIT), ("onnx", "onnx/onnx", TAG_COMMIT)]
    FILES = [("protobuf", "https://example.com/protobuf-1.tar.gz", "protobuf-protobuf-1.tar.gz", "ab" * 32)]

    def test_render(self):
        expected = f'''\
# Generated by scripts/update-onnxruntime-deps.py 1.2.3, do not edit.
# Third-party dependencies that ONNX Runtime's CMake would otherwise download
# during do_configure; see classes-recipe/onnxruntime-offline-deps.bbclass.

SRC_URI += " \\
    git://github.com/google/re2.git;protocol=https;nobranch=1;name=re2;destsuffix=ort-deps/re2 \\
    git://github.com/onnx/onnx.git;protocol=https;nobranch=1;name=onnx;destsuffix=ort-deps/onnx \\
    https://example.com/protobuf-1.tar.gz;downloadfilename=protobuf-protobuf-1.tar.gz;unpack=0;name=protobuf \\
"

SRCREV_re2 = "{COMMIT}"
SRCREV_onnx = "{TAG_COMMIT}"
SRC_URI[protobuf.sha256sum] = "{"ab" * 32}"
SRCREV_FORMAT .= "_re2_onnx"

# Dependencies fetched with git (checked out in UNPACKDIR/ort-deps/<name>)
ORT_DEPS_GIT = "\\
    re2 \\
    onnx \\
"

# Dependencies fetched as archives: <name>:<file in UNPACKDIR>
ORT_DEPS_FILES = "\\
    protobuf:protobuf-protobuf-1.tar.gz \\
"
'''
        self.assertEqual(u.render_inc("1.2.3", self.GIT, self.FILES), expected)

    def test_no_dependencies(self):
        out = u.render_inc("1.2.3", [], [])
        self.assertIn('SRC_URI += " \\\n"\n', out)
        self.assertIn('SRCREV_FORMAT .= ""\n', out)
        self.assertIn('ORT_DEPS_GIT = "\\\n"\n', out)
        self.assertIn('ORT_DEPS_FILES = "\\\n"\n', out)


class Main(unittest.TestCase):
    """Run main() with the downloads replaced, writing into a temporary layer."""

    def test_generates_inc(self):
        archive = b"archive"
        deps_txt = (
            "re2;https://github.com/google/re2/archive/refs/tags/2024-07-02.zip;1111\n"
            "protobuf;https://example.com/protobuf-1.tar.gz;" + hashlib.sha1(archive).hexdigest() + "\n"
            "unused;https://example.com/unused.zip;2222\n"
        ).encode()

        def run(*cmd):
            if cmd[:2] == ("curl", "-fsSL"):
                return deps_txt if cmd[2] == u.DEPS_TXT_URL.format("1.2.3") else archive
            return ls_remote(f"{COMMIT}\trefs/tags/2024-07-02")  # git ls-remote

        with tempfile.TemporaryDirectory() as layer:
            os.mkdir(os.path.join(layer, "conf"))
            script = os.path.join(layer, "scripts", "update-onnxruntime-deps.py")
            with mock.patch.object(u, "DEPS", ["re2", "protobuf"]), \
                    mock.patch.object(u, "run", side_effect=run), \
                    mock.patch.object(u, "__file__", script), \
                    mock.patch.object(sys, "argv", ["update-onnxruntime-deps.py", "1.2.3"]), \
                    mock.patch("builtins.print"):
                u.main()
            with open(os.path.join(layer, "conf", "onnxruntime-deps-1.2.3.inc")) as f:
                inc = f.read()

        self.assertEqual(inc, u.render_inc("1.2.3", [("re2", "google/re2", COMMIT)], [
            ("protobuf", "https://example.com/protobuf-1.tar.gz", "protobuf-protobuf-1.tar.gz",
             hashlib.sha256(archive).hexdigest())]))

    def test_sha1_mismatch_fails(self):
        deps_txt = b"protobuf;https://example.com/protobuf-1.tar.gz;" + b"0" * 40 + b"\n"

        def run(*cmd):
            return deps_txt if cmd[2] == u.DEPS_TXT_URL.format("1.2.3") else b"tampered"

        with mock.patch.object(u, "DEPS", ["protobuf"]), \
                mock.patch.object(u, "run", side_effect=run), \
                mock.patch.object(sys, "argv", ["update-onnxruntime-deps.py", "1.2.3"]):
            with self.assertRaises(SystemExit):
                u.main()

    def test_usage(self):
        with mock.patch.object(sys, "argv", ["update-onnxruntime-deps.py"]):
            with self.assertRaises(SystemExit) as cm:
                u.main()
        self.assertIn("Usage:", str(cm.exception))


if __name__ == "__main__":
    unittest.main()
