package com.ltu.progressbar.settings

import com.intellij.openapi.components.BaseState
import kotlinx.serialization.Serializable

const val CYCLE_TIME_DEFAULT: Int = 3000
const val REPAINT_INTERVAL_DEFAULT: Int = 50

@Serializable
class LtuProgressBarPersistentConfigs : BaseState() {
    var isAdvancedOptionsEnabled: Boolean by property(false)
    var cycleTime: Int by property(CYCLE_TIME_DEFAULT)
    var repaintInterval: Int by property(REPAINT_INTERVAL_DEFAULT)
}
