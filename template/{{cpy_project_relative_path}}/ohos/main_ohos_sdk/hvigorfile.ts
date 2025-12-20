import { harTasks } from '@ohos/hvigor-ohos-plugin';
import { archivePlugin } from '@ccgo/hvigor-ohos-plugin';

export default {
    system: harTasks,  /* Built-in plugin of Hvigor. It cannot be modified. */
    plugins: [
        archivePlugin()  /* Custom plugin to archive build artifacts */
    ]
}
