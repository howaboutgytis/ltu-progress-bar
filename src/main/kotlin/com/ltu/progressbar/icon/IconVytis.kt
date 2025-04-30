package com.ltu.progressbar.icon

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

interface IconVytis {
    companion object {
        const val SMALL_IMAGE_PATH = "icons/ltu-coat-small.png" // 32 x 32
//        const val MEDIUM_IMAGE_PATH = "icons/ltu-coat.png" // 128 x 128

        val SMALL: Icon = getIcon(SMALL_IMAGE_PATH, IconVytis::class.java)
//        val MEDIUM: Icon = getIcon(MEDIUM_IMAGE_PATH, IconVytis::class.java)
    }
}