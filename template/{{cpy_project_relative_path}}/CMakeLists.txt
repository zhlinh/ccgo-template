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

cmake_minimum_required(VERSION 3.14)

# main project root
set(MAIN_PROJECT_ROOT_DIR ${CMAKE_SOURCE_DIR})
string(REPLACE "/" ";" TEMP_SPLIT_ITEMS "${CMAKE_SOURCE_DIR}")
list(GET TEMP_SPLIT_ITEMS -1 MAIN_PROJECT_NAME)

project(${MAIN_PROJECT_NAME} CXX C)

# ========== Auto-detect if building as CCGO dependency ==========
# When this project is included as a third_party dependency by another CCGO project,
# the parent project will set CCGO_AS_DEPENDENCY=ON to disable standalone features
# like JNI_OnLoad, main entry points, etc.
if(CCGO_AS_DEPENDENCY)
    message(STATUS "[${PROJECT_NAME}] Building as CCGO dependency - disabling standalone features")
    # Define compile macro for conditional compilation
    add_compile_definitions(CCGO_AS_DEPENDENCY)
else()
    message(STATUS "[${PROJECT_NAME}] Building as standalone library")
endif()
# ================================================================

# CCGO_CMAKE_DIR is passed by build scripts pointing to ccgo package cmake directory
include(${CCGO_CMAKE_DIR}/template/Root.CMakeLists.txt.in)
