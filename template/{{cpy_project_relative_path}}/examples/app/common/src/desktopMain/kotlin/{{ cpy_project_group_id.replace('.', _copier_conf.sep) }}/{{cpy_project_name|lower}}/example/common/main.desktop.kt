package {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common.App

@Preview
@Composable
fun Application() {
    App()
}