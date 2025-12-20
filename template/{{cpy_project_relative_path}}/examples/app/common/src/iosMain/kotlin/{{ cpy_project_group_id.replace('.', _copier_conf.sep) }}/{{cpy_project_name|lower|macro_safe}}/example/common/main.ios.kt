package {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common

import androidx.compose.ui.window.Application
import {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    Application("Example Application") {
        App()
    }