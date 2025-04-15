package com.ltu.progressbar.listener

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.wm.IdeFrame
import com.ltu.progressbar.ui.LtuProgressBarUiExtensionPoint
import javax.swing.UIManager

class LtuProgressBarListener : LafManagerListener, ApplicationActivationListener {

    private val LTU_PROGRESS_BAR_UI_NAME = LtuProgressBarUiExtensionPoint::class.java.name

    override fun applicationActivated(ideFrame: IdeFrame) {
        updateProgressBarUi()
    }

    override fun lookAndFeelChanged(lafManager: LafManager) {
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBarUI", LTU_PROGRESS_BAR_UI_NAME)
        UIManager.getDefaults()[LTU_PROGRESS_BAR_UI_NAME] = LtuProgressBarUiExtensionPoint::class.java
    }

}