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
# Windows Native Stub CMakeLists.txt
# ============================================================================
# This CMakeLists.txt acts as a bridge between Visual Studio and the root project.
# It enables IDE features (IntelliSense, code navigation, debugging) without
# duplicating source code.
#
# Purpose:
# - Allows Visual Studio to open and index the project via CMake
# - Provides proper include paths for code completion
# - Enables debugging of C++ code in Visual Studio
#
# Usage:
# - Open the parent windows/ directory in Visual Studio (File → Open → Folder)
# - Or use cmake to generate Visual Studio solution from this directory
#
# ⚠️ Do not add source files to this directory - it's an IDE bridge only.
# ============================================================================

cmake_minimum_required(VERSION 3.15)

# Project name matches the root project
project({{cpy_project_name}}_windows_stub CXX C)

# Add the root project as a subdirectory
# The root CMakeLists.txt is two levels up (windows/windows_native_stub -> root)
add_subdirectory(../.. ${CMAKE_BINARY_DIR}/root_project)

# Message for IDE users
message(STATUS "")
message(STATUS "=================================================================")
message(STATUS "  Windows Native Stub for {{cpy_project_name}}")
message(STATUS "=================================================================")
message(STATUS "  This is an IDE bridge for Visual Studio integration.")
message(STATUS "  Source files are in the root project directory.")
message(STATUS "  Use 'ccgo build windows' for production builds.")
message(STATUS "=================================================================")
message(STATUS "")
