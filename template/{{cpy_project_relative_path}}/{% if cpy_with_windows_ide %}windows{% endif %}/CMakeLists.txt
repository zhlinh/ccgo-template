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

# ============================================================================
# Windows IDE Integration CMakeLists.txt
# ============================================================================
# This file enables Visual Studio and VS Code to open this directory
# as a CMake project and access all source files from the parent project.
#
# Usage:
# - Visual Studio: File → Open → Folder → Select this windows/ directory
# - VS Code: Open this directory with CMake Tools extension
#
# For production builds, use: ccgo build windows
# ============================================================================

cmake_minimum_required(VERSION 3.15)

project({{cpy_project_name}} CXX C)

# Include the root project
# The root CMakeLists.txt is one level up
add_subdirectory(.. ${CMAKE_BINARY_DIR}/root_project)
