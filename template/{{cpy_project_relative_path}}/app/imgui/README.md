# ImGui Desktop Demo

From the directory containing `CCGO.toml`:

```bash
ccgo run --app imgui-demo
ccgo run --app imgui-demo --release --build-only
```

The demo uses Dear ImGui 1.91.9b, GLFW 3.4 and OpenGL. The first configure
downloads the two source dependencies through CMake FetchContent and requires
Git and network access. Later builds reuse the local dependency sources.
For offline builds, pass CMake's `FETCHCONTENT_SOURCE_DIR_GLFW` and
`FETCHCONTENT_SOURCE_DIR_IMGUI` through the app's `build_args` in `CCGO.toml`.

Install CMake and a desktop C++ compiler. Linux also needs OpenGL and X11
development packages (including XRandR, Xinerama, XCursor and XInput). Windows
can use Visual Studio's compiler. macOS requires its command-line developer tools.
A graphical session is required to run the demo; `--build-only` does not open a window.

This is a standalone UI starter. Add your SDK libraries to CMakeLists.txt and
replace the demo button handler with SDK calls when extending it. It does not
automatically build/import the project SDK or create mobile application packages.
