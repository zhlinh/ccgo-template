import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mojet.app.sample.common.Application


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
       Application()
    }
}
