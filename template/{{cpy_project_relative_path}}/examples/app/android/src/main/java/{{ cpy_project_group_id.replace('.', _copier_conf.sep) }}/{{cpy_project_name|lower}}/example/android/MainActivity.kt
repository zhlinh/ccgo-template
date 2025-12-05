package {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common.Application

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Application()
            }
        }
    }
}