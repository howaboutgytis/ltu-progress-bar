package com.ltu.progressbar.ui

import com.intellij.openapi.components.service
import com.ltu.progressbar.settings.LtuProgressBarPersistentConfigsComponent
import javax.swing.UIManager

open class LtuProgressBarDemoUi : LtuProgressBarUi() {

    private var currentDemo = service<LtuProgressBarPersistentConfigsComponent>().state

    override fun installDefaults() {
        super.installDefaults()
        UIManager.put("ProgressBar.repaintInterval", currentDemo.repaintInterval)
        UIManager.put("ProgressBar.cycleTime", currentDemo.cycleTime)
    }
}
