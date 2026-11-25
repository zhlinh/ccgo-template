#!/usr/bin/env python3
"""Render the C++ starter/config and verify them with a local ccgo binary.

Requires Jinja2 (also installed by Copier), CMake and a host C++ compiler.
Run: python3 scripts/test-cpp-example.py --ccgo /path/to/ccgo
"""

import argparse
from pathlib import Path
import subprocess
import tempfile

from jinja2 import Environment, FileSystemLoader, StrictUndefined


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ccgo", required=True, type=Path)
    parser.add_argument(
        "--build-imgui",
        action="store_true",
        help="Also fetch/build the ImGui app without opening a window",
    )
    parser.add_argument(
        "--glfw-source", type=Path, help="Optional local GLFW source tree"
    )
    parser.add_argument(
        "--imgui-source", type=Path, help="Optional local Dear ImGui source tree"
    )
    args = parser.parse_args()
    ccgo = args.ccgo.resolve()
    template = (
        Path(__file__).resolve().parents[1] / "template/{{cpy_project_relative_path}}"
    )
    environment = Environment(
        loader=FileSystemLoader(str(template)), undefined=StrictUndefined
    )
    context = dict(
        cpy_project_name="ExampleDemo",
        cpy_project_version="1.0.0",
        cpy_project_group_id="com.example",
        cpy_git_repo_url="",
        cpy_user_name="Example Author",
        cpy_user_email="example@example.com",
        cpy_project_dependencies="EMPTY",
    )
    with tempfile.TemporaryDirectory(prefix="ccgo-template-example-") as temporary:
        root = Path(temporary)
        for relative in ["CCGO.toml", "app/basic/main.cpp", "app/imgui/main.cpp"]:
            destination = root / relative
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_text(
                environment.get_template(relative + ".jinja").render(**context)
            )
        destination = root / "app/imgui/CMakeLists.txt"
        destination.write_text((template / "app/imgui/CMakeLists.txt").read_text())
        cmake_args = []
        for dependency in ["glfw", "imgui"]:
            source = getattr(args, dependency + "_source")
            if source:
                cmake_args.append(
                    "-DFETCHCONTENT_SOURCE_DIR_"
                    + dependency.upper()
                    + "="
                    + source.resolve().as_posix()
                )
        if cmake_args:
            # JSON strings are also valid TOML basic strings.
            import json

            config = root / "CCGO.toml"
            config.write_text(
                config.read_text().replace(
                    'target = "imgui_demo"',
                    'target = "imgui_demo"\nbuild_args = ' + json.dumps(cmake_args),
                )
            )
        for options in [
            ["--app", "basic", "--", "hello"],
            [],
            ["--app", "basic", "--release", "--build-only"],
        ]:
            result = subprocess.run(
                [str(ccgo), "run", *options], cwd=root, text=True, capture_output=True
            )
            assert result.returncode == 0, result.stdout + result.stderr
            if "--build-only" not in options:
                assert "ExampleDemo C++ example" in result.stdout
            if "hello" in options:
                assert "Argument: hello" in result.stdout
        print(
            "Template C++ app passed: explicit/default target, arguments, release build-only"
        )
        if args.build_imgui:
            subprocess.run(
                [str(ccgo), "run", "--app", "imgui-demo", "--release", "--build-only"],
                cwd=root,
                check=True,
            )
            print(
                "Template ImGui app passed: configure and release build; no GUI launched"
            )


if __name__ == "__main__":
    main()
