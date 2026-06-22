package hu.bozgab.megaclient.util

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker

actual suspend fun getImage(): PlatformFile? {
    return FileKit.openFilePicker(type = FileKitType.Image)
}
