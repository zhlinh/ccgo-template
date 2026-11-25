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

Enter text and press **Call SDK** (or Enter). The app calls the project's
`Echo` function in `src/api/native/echo.cc` and displays `<project name>: <input>`.
The app links that source as a separate static library, sharing the public header
under `include/` with SDK consumers. Generate the project with source/include
directories enabled (the default).

The interface follows Dear ImGui's official fullscreen and child-window examples:
a warm paper palette, a teal sidebar, Karla headings and Cousine output text.
Wide windows show request and response side by side; smaller windows stack them
and hide the sidebar. Copy results, try preset inputs, and review the last 20 calls
with their measured SDK execution times. Fonts come from the pinned ImGui source
dependency; no extra font download is required.

The same executable can verify the SDK call without opening a window:

```bash
ccgo run --app imgui-demo -- --echo hello
```

This builds the demo SDK component from source; it does not import a packaged
SDK or create mobile application packages.
