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

message(STATUS "==============config ${PROJECT_NAME}====================")

include(${CMAKE_SOURCE_DIR}/cmake/CMakeConfig.cmake)
include(${CMAKE_SOURCE_DIR}/cmake/CMakeFunctions.cmake)

message(STATUS "==============CMakeConfig ${CMAKE_SOURCE_DIR}/cmake/CMakeConfig.cmake====================")

# just debug;release
set(CMAKE_CONFIGURATION_TYPES "Debug;Release" CACHE STRING "" FORCE)

set_property(GLOBAL PROPERTY USE_FOLDERS ON)
# include root dir
include_directories(${CMAKE_SOURCE_DIR}/)
# include root 'include' dir
include_directories(${CMAKE_SOURCE_DIR}/include)
# include root 'src' dir
include_directories(${CMAKE_SOURCE_DIR}/src)

# export compile_commands.json
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
# set visibility
set(CMAKE_CXX_VISIBILITY_PRESET default)
set(CMAKE_C_VISIBILITY_PRESET default)

if(COMM_USE_SYSTEM_INCLUDES)
    set(COMM_INTERNAL_INCLUDE_WARNING_GUARD SYSTEM)
else()
    set(COMM_INTERNAL_INCLUDE_WARNING_GUARD "")
endif()

# add_subdirectory的子模块才INSTALL
if(NOT CMAKE_SOURCE_DIR STREQUAL PROJECT_SOURCE_DIR)
    option(COMM_ENABLE_INSTALL "Enable install rule" OFF)
else()
    option(COMM_ENABLE_INSTALL "Enable install rule" ON)
endif()

# set ide folder
if(NOT DEFINED COMM_IDE_FOLDER)
    set(COMM_IDE_FOLDER ${MAIN_PROJECT_NAME})
endif()

option(BUILD_CHANNEL_OVERSEA "build vpncomm channel oversea" OFF)
# enable export
add_definitions(-DCOMM_ENABLE_EXPORTS=1)

if(BUILD_CHANNEL_OVERSEA)
    add_definitions(-DCOMM_BUILD_CHANNEL_OVERSEA=1)
endif()

macro(add_third_party_option conf_name desc value)
  message(STATUS "add_third_party_option ${conf_name} '${desc}' ${value}")
  option(${conf_name} ${desc} ${value})
  if(${value})
    option(THIRD_PARTY_OPTION "third party option" ON)
  endif()
endmacro()

# The prefix for logging tags, usually the project name.
if(COMM_TAG_PREFIX)
    add_definitions(-DCOMM_TAG_PREFIX=\"${COMM_TAG_PREFIX}\")
else()
    add_definitions(-DCOMM_TAG_PREFIX=\"${MAIN_PROJECT_NAME}\")
endif()

# The suffix for logging tags, usually the build time.
if(COMM_LOG_TAG_SUFFIX)
    add_definitions(-DCOMM_LOG_TAG_SUFFIX=\"${COMM_LOG_TAG_SUFFIX}\")
endif()

if (NOT COMM_REVISION)
    # get revision
    execute_process(COMMAND git rev-parse --short HEAD
                    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                    OUTPUT_VARIABLE COMM_REVISION
                    OUTPUT_STRIP_TRAILING_WHITESPACE)
endif()

if(COMM_REVISION)
  add_definitions(-DCOMM_REVISION=\"${COMM_REVISION}\")
endif()

# logcomm does not have source code, just use option
option(LOGCOMM_SUPPORT "use logcomm provided logfile support" ON)
option(FOUNDRYCOMM_SUPPORT "use foundrycomm provided base support" ON)

# rapidjson仅头文件
option(RAPIDJSON_SUPPORT "use rapidjson provided json support" ON)

# 此处遍历third_party目录下的所有子目录，如果子目录下存在src目录，则认为是一个有效的第三方库
file(GLOB third_party_children RELATIVE
        "${CMAKE_SOURCE_DIR}/third_party/"
        ${CMAKE_SOURCE_DIR}/third_party/*
)
foreach(dir ${third_party_children})
    if(IS_DIRECTORY ${CMAKE_SOURCE_DIR}/third_party/${dir})
        if(EXISTS ${CMAKE_SOURCE_DIR}/third_party/${dir}/src)
            string(TOUPPER ${dir} upper_dir)
            add_third_party_option(${upper_dir}_SUPPORT "use ${upper_dir} support" ON)
        endif()
    endif()
endforeach()

if(EXISTS ${CMAKE_SOURCE_DIR}/third_party/zlib/src/)
    add_third_party_option(ZLIB_SUPPORT "use zlib minizip support" ON)
endif()
add_third_party_option(GOOGLETEST_SUPPORT "use googletest provided cpp unittest support" OFF)
add_third_party_option(BENCHMARK_SUPPORT "use googletest provided benchmark support" OFF)

# include ios api
include_directories(${CMAKE_SOURCE_DIR}/include/${MAIN_PROJECT_NAME}/api/apple/)
include_directories(${CMAKE_SOURCE_DIR}/include/${MAIN_PROJECT_NAME}/api/ios/)
include_directories(${CMAKE_SOURCE_DIR}/include/${MAIN_PROJECT_NAME}/api/macos/)
# include third party include dir
set(COMM_THIRD_PARTY_INCLUDE_DIRS "")
get_third_party_include_directories(COMM_THIRD_PARTY_INCLUDE_DIRS ${CMAKE_SOURCE_DIR}/third_party/)
foreach(dir ${COMM_THIRD_PARTY_INCLUDE_DIRS})
    set(third_party_sub_dir ${CMAKE_SOURCE_DIR}/third_party/${dir})
    include_directories(${third_party_sub_dir}/include/)
endforeach()

if(MSVC)
    include_directories(${CMAKE_SOURCE_DIR}/third_party/pthread/src/windows)
    # for pthread
    add_definitions(-D__PTW32_STATIC_LIB=1)
endif()

# google test or benchmark, then disable log
#if (GOOGLETEST_SUPPORT OR BENCHMARK_SUPPORT)
#    add_definitions(-DCOMM_DISABLE_LOG=1)
#endif()

if(GOOGLETEST_SUPPORT)
    add_definitions(-DCOMM_ENABLE_GOOGLETEST=1)
    set(ENABLE_VISIBILITY 1)
endif()

if(BENCHMARK_SUPPORT)
    add_definitions(-DCOMM_ENABLE_BENCHMARK=1)
    set(ENABLE_VISIBILITY 1)
endif()

if(LOGCOMM_SUPPORT)
    add_definitions(-DCOMM_ENABLE_LOGCOMM=1)
endif()

set(CMAKE_CXX_STANDARD ${CONFIG_COMM_CMAKE_CXX_STANDARD})
set(CMAKE_CXX_STANDARD_REQUIRED ON)

if(ANDROID)
    if (NOT PROTOBUF_SUPPORT)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
    endif()

    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++${CONFIG_COMM_CMAKE_CXX_STANDARD}")

    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0 -DDEBUG")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fexceptions")

    # Don't re-export libgcc symbols in every binary.
    list(APPEND SELF_LINKER_FLAGS -Wl,--exclude-libs,libgcc.a)
    # arm32 currently uses a linker script in place of libgcc to ensure that
    # libunwind is linked in the correct order. --exclude-libs does not propagate to
    # the contents of the linker script and can't be specified within the linker
    # script. Hide both regardless of architecture to future-proof us in case we
    # move other architectures to a linker script (which we may want to do so we
    # automatically link libclangrt on other architectures).
    list(APPEND SELF_LINKER_FLAGS -Wl,--exclude-libs,libgcc_real.a)
    list(APPEND SELF_LINKER_FLAGS -Wl,--exclude-libs,libatomic.a)
    # STL specific flags.
    if(ANDROID_STL MATCHES "^c\\+\\+_")
        if(ANDROID_ABI MATCHES "^armeabi")
            list(APPEND SELF_LINKER_FLAGS "-Wl,--exclude-libs,libunwind.a")
        endif()
    endif()

    set(CMAKE_SHARED_LINKER_FLAGS "${SELF_LINKER_FLAGS} ${CMAKE_SHARED_LINKER_FLAGS}")
    set(CMAKE_MODULE_LINKER_FLAGS "${SELF_LINKER_FLAGS} ${CMAKE_MODULE_LINKER_FLAGS}")
    set(CMAKE_EXE_LINKER_FLAGS    "${SELF_LINKER_FLAGS} ${CMAKE_EXE_LINKER_FLAGS}")

elseif(APPLE)
    # for gen xcode project file
    set(CMAKE_XCODE_ATTRIBUTE_CLANG_CXX_LANGUAGE_STANDARD "gnu++${CONFIG_COMM_CMAKE_CXX_STANDARD}")
    set(CMAKE_XCODE_ATTRIBUTE_CLANG_CXX_LIBRARY "libc++")

    # for build
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++${CONFIG_COMM_CMAKE_CXX_STANDARD}")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")
    set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -g -gline-tables-only -Os")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -g -gline-tables-only -Os")

    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0 -DDEBUG")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fexceptions")
    set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -g -O0")
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -O0")

    if (NOT DEFINED ENABLE_BITCODE)
    # Unless specified, enable BITCODE support by default
        set(ENABLE_BITCODE TRUE CACHE BOOL "[DEFAULTS] Enabling BITCODE support by default.")
    endif()
    if (ENABLE_BITCODE)
        set(CMAKE_XCODE_ATTRIBUTE_ENABLE_BITCODE "YES")
    else()
        set(CMAKE_XCODE_ATTRIBUTE_ENABLE_BITCODE "NO")
    endif()
    
    # Use ARC or not
    if(NOT DEFINED ENABLE_ARC)
        # Unless specified, enable ARC support by default
        set(ENABLE_ARC TRUE CACHE BOOL "[DEFAULTS] Enabling ARC support by default.")
    endif()
    if(ENABLE_ARC)
        set(CMAKE_OBJC_FLAGS "-fobjc-arc ${CMAKE_OBJC_FLAGS}" CACHE INTERNAL "Flags used by the compiler during all OBJC build types.")
        set(CMAKE_XCODE_ATTRIBUTE_CLANG_ENABLE_OBJC_ARC "YES")
    else()
        set(CMAKE_OBJC_FLAGS "-fno-objc-arc ${CMAKE_OBJC_FLAGS}" CACHE INTERNAL "Flags used by the compiler during all OBJC build types.")
        set(CMAKE_XCODE_ATTRIBUTE_CLANG_ENABLE_OBJC_ARC "NO")
    endif()

    set(CMAKE_XCODE_ATTRIBUTE_STRIP_STYLE "all")
    set(CMAKE_XCODE_ATTRIBUTE_IPHONEOS_DEPLOYMENT_TARGET "10.0")
    set(CMAKE_XCODE_ATTRIBUTE_OTHER_LDFLAGS "-ObjC")
    set(CMAKE_XCODE_ATTRIBUTE_DEBUG_INFORMATION_FORMAT "dwarf-with-dsym")
    set(CMAKE_XCODE_ATTRIBUTE_CLANG_DEBUG_INFORMATION_LEVEL[variant=Debug] "default")
    set(CMAKE_XCODE_ATTRIBUTE_CLANG_DEBUG_INFORMATION_LEVEL[variant=Release] "line-tables-only")

    if(DEFINED IOS_DEPLOYMENT_TARGET)
        message(STATUS "setting IOS_DEPLOYMENT_TARGET=${IOS_DEPLOYMENT_TARGET}")
        set(CMAKE_XCODE_ATTRIBUTE_IPHONEOS_DEPLOYMENT_TARGET "${IOS_DEPLOYMENT_TARGET}")
    endif()

elseif(MSVC)
    add_definitions(-DCMAKE_CXX_STANDARD=${CONFIG_COMM_CMAKE_CXX_STANDARD})
    # pthreadVC2 import by static
    add_definitions(-DPTW32_STATIC_LIB)

    # add DEBUG macro .. release has NDEBUG defaultly
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} /DDEBUG")

    if(CMAKE_CL_64)
        add_definitions(-D_WIN64 -DWIN64)
    endif()

    add_definitions(-D_WIN32 -DWIN32 -DUNICODE -D_UNICODE -DNOMINMAX -D_LIB)
    #set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /MP /Zc:threadSafeInit-")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /MP")

    # generate pdb file
    # /Z7：会生成包含调试信息的目标文件，但是不会生成程序数据库（.pdb）文件。这个选项可以用来在没有 Visual Studio 的情况下进行调试，但是会增加目标文件的大小。
    # /Zi：会生成包含调试信息的程序数据库（.pdb）文件，以便在调试时使用。这个选项只在 Release 模式下添加，因为在 Debug 模式下，CMake 默认会添加 /Zi 选项。
    # /Zd：会生成包含调试信息的目标文件，并将调试信息嵌入到目标文件中。这个选项可以用来在没有 Visual Studio 的情况下进行调试，但是会增加目标文件的大小。
    # /ZI：这个选项类似于 /Zi，但是会在编译时生成一个预编译头文件（.pch）来加快编译速度。这个选项只在 Release 模式下添加，因为在 Debug 模式下，CMake 默认会添加 /ZI 选项。
    if(BENCHMARK_SUPPORT OR BENCHMARK_SUPPORT)
        set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /Z7")
        set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} /Z7")
        set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} /DEBUG:FULL")
    else()
        set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /Zi")
        set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} /Zi")
        set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} /DEBUG /OPT:REF /OPT:ICF")
    endif()

    set(CompilerFlags
            CMAKE_CXX_FLAGS
            CMAKE_CXX_FLAGS_DEBUG
            CMAKE_CXX_FLAGS_RELEASE
            CMAKE_C_FLAGS
            CMAKE_C_FLAGS_DEBUG
            CMAKE_C_FLAGS_RELEASE)

    foreach(CompilerFlag ${CompilerFlags})
        string(REPLACE "/MD" "/MT" ${CompilerFlag} "${${CompilerFlag}}")
    endforeach()

    add_compile_options("$<$<C_COMPILER_ID:MSVC>:/utf-8>")
    add_compile_options("$<$<CXX_COMPILER_ID:MSVC>:/utf-8>")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} /FORCE:MULTIPLE")

elseif(UNIX)
    add_definitions(-D__linux__ -Dlinux -D__linux)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++${CONFIG_COMM_CMAKE_CXX_STANDARD}")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fexceptions")

    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -ffunction-sections -fdata-sections -Os")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC -ffunction-sections -fdata-sections -Os")

    set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -g")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -g")

    set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -g -O0")
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -O0")
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0 -DDEBUG")

    # for error of .rodata can not be used when making a PIE object; recompile with -fPIC
    SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -no-pie")
    # 判断是否使用 GNU 工具链
    if(CMAKE_C_COMPILER_ID STREQUAL "GNU")
        # 添加链接选项
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,--no-undefined -Wl,--allow-multiple-definition")
    endif()
endif()
