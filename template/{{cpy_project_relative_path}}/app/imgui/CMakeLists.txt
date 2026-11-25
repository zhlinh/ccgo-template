cmake_minimum_required(VERSION 3.16)
project(imgui_demo LANGUAGES C CXX)

include(FetchContent)
set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_WAYLAND OFF CACHE BOOL "" FORCE)
FetchContent_Declare(glfw
    GIT_REPOSITORY https://github.com/glfw/glfw.git
    GIT_TAG 3.4
    GIT_SHALLOW TRUE
)
FetchContent_Declare(imgui
    GIT_REPOSITORY https://github.com/ocornut/imgui.git
    GIT_TAG v1.91.9b
    GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(glfw imgui)
find_package(OpenGL REQUIRED)

add_library(imgui_backend STATIC
    ${imgui_SOURCE_DIR}/imgui.cpp
    ${imgui_SOURCE_DIR}/imgui_draw.cpp
    ${imgui_SOURCE_DIR}/imgui_tables.cpp
    ${imgui_SOURCE_DIR}/imgui_widgets.cpp
    ${imgui_SOURCE_DIR}/backends/imgui_impl_glfw.cpp
    ${imgui_SOURCE_DIR}/backends/imgui_impl_opengl3.cpp
)
target_include_directories(imgui_backend PUBLIC ${imgui_SOURCE_DIR} ${imgui_SOURCE_DIR}/backends)
target_link_libraries(imgui_backend PUBLIC glfw OpenGL::GL ${CMAKE_DL_LIBS})
target_compile_features(imgui_backend PUBLIC cxx_std_17)

add_executable(imgui_demo main.cpp)
target_compile_definitions(imgui_demo PRIVATE DEMO_FONT_DIR="${imgui_SOURCE_DIR}/misc/fonts")
get_filename_component(SDK_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../.." ABSOLUTE)
add_library(project_echo STATIC "${SDK_ROOT}/src/api/native/echo.cc")
target_include_directories(project_echo PUBLIC "${SDK_ROOT}/include")
target_compile_features(project_echo PUBLIC cxx_std_17)
target_link_libraries(imgui_demo PRIVATE imgui_backend project_echo)
