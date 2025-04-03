#!/usr/bin/env python3
# -- coding: utf-8 --
#
# build_ohos.py
# ccgo
#
# Copyright 2024 zhlinh and ccgo Project Authors. All rights reserved.
# Use of this source code is governed by a MIT-style
# license that can be found at
#
# https://opensource.org/license/MIT
#
# The above copyright notice and this permission
# notice shall be included in all copies or
# substantial portions of the Software.

import glob
import os
import platform
import shutil
import sys
import time

from build_utils import *


SCRIPT_PATH = os.path.split(os.path.realpath(__file__))[0]
# 以当前的目录名的大写作为项目名
PROJECT_NAME = os.path.basename(SCRIPT_PATH).upper()
PROJECT_NAME_LOWER = PROJECT_NAME.lower()
PROJECT_RELATIVE_PATH = PROJECT_NAME.lower()

if system_is_windows():
    OHOS_GENERATOR = '-G "Unix Makefiles"'
else:
    OHOS_GENERATOR = ''

try:
    OHOS_SDK_ROOT = os.environ['OHOS_SDK_HOME'] or os.environ['HOS_SDK_HOME']
except KeyError as identifier:
    OHOS_SDK_ROOT = ''

BUILD_OUT_PATH = 'cmake_build/ohos'
OHOS_LIBS_INSTALL_PATH = BUILD_OUT_PATH + '/'
OHOS_BUILD_CMD = ('cmake "%s" %s -DOHOS_ARCH="%s" '
                     '-DOHOS=1'
                     '-D__OHOS__=1'
                     '-DCMAKE_BUILD_TYPE=Release '
                     '-DOHOS_PLATFORM=OHOS '
                     '-DCMAKE_TOOLCHAIN_FILE=%s/native/build/cmake/ohos.toolchain.cmake '
                     '-DOHOS_TOOLCHAIN=clang '
                     '-DOHOS_SDK_NATIVE=%s/native/ '
                     '-DOHOS_SDK_NATIVE_PLATFORM=ohos-%s '
                     '-DOHOS_STL="%s" %s '
                     '&& cmake --build . --config Release -- -j8')
OHOS_SYMBOL_PATH = f'{OHOS_PROJECT_PATH}/obj/local/'
OHOS_LIBS_PATH = f'{OHOS_PROJECT_PATH}/libs/'

OHOS_STRIP_FILE = OHOS_SDK_ROOT + f"/native/llvm/bin/llvm-strip"

# stl files
OHOS_STL_FILE = {
    'armeabi-v7a': OHOS_SDK_ROOT + f"/native/llvm/lib/arm-linux-ohos/libc++_shared.so",
    'arm64-v8a': OHOS_SDK_ROOT + f"/native/llvm/lib/aarch64-linux-ohos/libc++_shared.so",
    'x86_64': OHOS_SDK_ROOT + f"/native/llvm/lib/x86_64-linux-ohos/libc++_shared.so",
}


def get_ohos_strip_path(arch):
    strip_path = OHOS_STRIP_FILE
    return strip_path


def build_ohos(incremental, arch, target_option, tag):
    before_time = time.time()

    clean(os.path.join(SCRIPT_PATH, BUILD_OUT_PATH), incremental)
    os.chdir(os.path.join(SCRIPT_PATH, BUILD_OUT_PATH))

    build_cmd = OHOS_BUILD_CMD % (
        SCRIPT_PATH, OHOS_GENERATOR, arch, OHOS_SDK_ROOT, OHOS_SDK_ROOT,
        get_ohos_min_sdk_version(SCRIPT_PATH), get_ohos_stl(SCRIPT_PATH), target_option)
    print(f"build cmd: [{build_cmd}]")
    ret = os.system(build_cmd)
    os.chdir(SCRIPT_PATH)

    if 0 != ret:
        print('!!!!!!!!!!!!!!!!!!build fail!!!!!!!!!!!!!!!!!!!!')
        return False

    symbol_path = OHOS_SYMBOL_PATH
    lib_path = OHOS_LIBS_PATH

    if not os.path.exists(symbol_path):
        os.makedirs(symbol_path)

    symbol_path = symbol_path + arch
    if os.path.exists(symbol_path):
        shutil.rmtree(symbol_path)

    os.mkdir(symbol_path)

    if not os.path.exists(lib_path):
        os.makedirs(lib_path)

    lib_path = lib_path + arch
    if os.path.exists(lib_path):
        shutil.rmtree(lib_path)

    os.mkdir(lib_path)

    for f in glob.glob(OHOS_LIBS_INSTALL_PATH + "*.so"):
        if is_in_lib_list(f, OHOS_MERGE_EXCLUDE_LIBS):
            continue
        shutil.copy(f, symbol_path)
        shutil.copy(f, lib_path)

    if os.path.exists('third_party'):
        if "stdcomm" not in os.listdir('third_party'):
            # copy stl
            shutil.copy(OHOS_STL_FILE[arch], symbol_path)
            shutil.copy(OHOS_STL_FILE[arch], lib_path)

        # copy third_party/xxx/lib/ohos/yyy/*.so
        for f in os.listdir('third_party'):
            if f.endswith("comm") and (f not in OHOS_MERGE_THIRD_PARTY_LIBS):
                # xxxcomm is not default to merge
                continue
            target_dir = f'third_party/{f}/lib/ohos/{arch}/'
            if not os.path.exists(target_dir):
                continue
            file_names = glob.glob(target_dir + "*.so")
            for file_name in file_names:
                if is_in_lib_list(file_name, OHOS_MERGE_EXCLUDE_LIBS):
                    continue
                shutil.copy(file_name, lib_path)

    # strip
    strip_path = get_ohos_strip_path(arch)
    for f in glob.glob(f"{lib_path}/*.so"):
        strip_cmd = f"{strip_path} {f}"
        print(f"strip cmd: [{strip_cmd}]")
        os.system(strip_cmd)

    print(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
    print(f'==================[{arch}] Output========================')
    print(f'libs(release): {lib_path}')
    print(f'symbols(must store permanently): {symbol_path}')

    after_time = time.time()

    print(f"use time: {int(after_time - before_time)}")
    return True


def main(incremental, build_archs, target_option='', tag=''):
    if not check_ohos_native_env():
        raise RuntimeError(f"Exception occurs when check ohos native env, please install ndk {get_ohos_native_desc()} and put in env OHOS_SDK_HOME")

    print(f"main tag {tag}, archs [{build_archs}]")

    # generate verinfo.h
    gen_project_revision_file(PROJECT_NAME, OUTPUT_VERINFO_PATH, get_version_name(SCRIPT_PATH), tag,
                              incremental=incremental)

    has_error = False
    success_archs = []
    for arch in build_archs:
        if not build_ohos(incremental, arch, target_option, tag):
            has_error = True
            break
        success_archs.append(arch)
    print('==================OHOS Build Done========================')
    print(f'Build All:{build_archs}')
    print(f'Build Success:{success_archs}')
    print(f'Build Failed:{list(set(build_archs) - set(success_archs))}')
    print('==================Output========================')
    print(f'libs(release): {OHOS_LIBS_PATH}')
    print(f'symbols(must store permanently): {OHOS_SYMBOL_PATH}')
    if has_error:
        raise RuntimeError("Exception occurs when build ohos")


if __name__ == '__main__':
    while True:
        if len(sys.argv) >= 3:
            archs = sys.argv[2:]
            num = sys.argv[1]
        elif len(sys.argv) == 2:
            num = sys.argv[1]
            archs = set(['armeabi-v7a', 'arm64-v8a', 'x86_64'])
        else:
            archs = set(['armeabi-v7a'])
            num = str(input(
                'Enter menu:\n'
                + f'1. Clean && build {PROJECT_NAME_LOWER}.\n'
                + f'2. Build incrementally {PROJECT_NAME_LOWER}.\n'
                + '3. Build for so test.\n'
                + '4. Exit.\n'))
        print(f'==================OHOS(Open Harmony) Choose num: {num}, archs: [{archs}]==================')
        if num == '1':
            main(False, archs, tag=num)
            break
        elif num == '2':
            main(True, archs, tag=num)
            break
        elif num == '3':
            cur_time = time.strftime("%H%M%S", time.localtime())
            # if test, then set it incremental
            main(True, archs,
                 target_option=f"-DCOMM_LOG_TAG_SUFFIX={cur_time}",
                 tag=num)
            break
        else:
            break
