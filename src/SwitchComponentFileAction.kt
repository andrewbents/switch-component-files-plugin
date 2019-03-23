import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.apache.commons.io.FilenameUtils
import java.util.*

private fun getFileByPath(path: String): VirtualFile? =
    LocalFileSystem.getInstance().findFileByPath(path)

private val extensions: List<String> = Arrays.asList(
    "ts", "tsx", "js", "coffee",
    "html", "php", "haml", "jade", "pug", "slim",
    "css", "sass", "scss", "less", "styl"
)

class SwitchComponentFileAction : AnAction("SwitchComponentFile") {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        val currentFile = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val nextFile = this.nextFile(currentFile) ?: return

        this.openFile(project, nextFile)
    }

    private fun nextFile(currentFile: VirtualFile): VirtualFile? {
        val currentFilePath = currentFile.canonicalPath ?: return null

        val basePath = FilenameUtils.removeExtension(currentFilePath)
        val currentExtension = FilenameUtils.getExtension(currentFilePath)

        val currentFileIndex = extensions.indexOf(currentExtension)
        if (currentFileIndex == -1) return null

        for (i in extensions.indices) {
            val nextExtension = extensions[(currentFileIndex + 1 + i) % extensions.size]
            val nextFile = getFileByPath("$basePath.$nextExtension") ?: continue
            if (nextFile.exists()) {
                return nextFile
            }
        }
        return null
    }

    private fun openFile(project: Project, file: VirtualFile) {
        if (file.exists()) {
            OpenFileDescriptor(project, file).navigate(true)
        }
    }
}
