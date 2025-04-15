package com.ltu.progressbar.icon

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon


interface IconVytis {
    companion object {
        val SMALL: Icon = getIcon("icons/ltu-coat-small.png", IconVytis::class.java)
    }
}