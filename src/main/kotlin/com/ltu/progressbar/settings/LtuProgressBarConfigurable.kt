package com.ltu.progressbar.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindValue
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.selected
import com.ltu.progressbar.ui.LtuProgressBarDemoUi
import kotlinx.coroutines.*
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JProgressBar
import javax.swing.JSlider


class LtuProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

    private val getConfig = service<LtuProgressBarPersistentConfigsComponent>()
    private val getDemoConfig = service<LtuProgressBarPersistentConfigsComponent>()
    private var initial = getConfig.state
    private var current = getDemoConfig.state

    private val indeterminateExampleProgressBar = JProgressBar()
    private val determinateExampleProgressBar = JProgressBar()

    override val coroutineContext = CoroutineScope(Job()).coroutineContext

    private lateinit var panel: DialogPanel
    private lateinit var advancedOptionsCheckBox: JCheckBox
    private lateinit var cycleTimeSlider: JSlider
    private lateinit var repaintIntervalSlider: JSlider

    init {
        indeterminateExampleProgressBar.setUI(LtuProgressBarDemoUi())
        indeterminateExampleProgressBar.isIndeterminate = true

        determinateExampleProgressBar.setUI(LtuProgressBarDemoUi())
        determinateExampleProgressBar.isIndeterminate = false
        determinateExampleProgressBar.minimum = 0
        determinateExampleProgressBar.maximum = 100
        determinateExampleProgressBar.value = 0
    }

    override fun createComponent(): JComponent {
        panel = panel {
            group("Indeterminate") {
                panel {
                    row {
                        cell(indeterminateExampleProgressBar)
                    }
                    row {
                        advancedOptionsCheckBox = checkBox("Advanced")
                            .bindSelected(current::isAdvancedOptionsEnabled)
                            .component
                        advancedOptionsCheckBox.addChangeListener {
                            current.isAdvancedOptionsEnabled = advancedOptionsCheckBox.isSelected
                            if (advancedOptionsCheckBox.isSelected.not()) {
                                cycleTimeSlider.value = CYCLE_TIME_DEFAULT
                                repaintIntervalSlider.value = REPAINT_INTERVAL_DEFAULT
                            }
                        }
                    }
                    indent {
                        row("Cycle Time (ms):") {
                            cycleTimeSlider = slider(0, 3000, 500, 1000)
                                .bindValue(current::cycleTime)
                                .component
                            cycleTimeSlider.addChangeListener {
                                current.cycleTime = cycleTimeSlider.value
                                indeterminateExampleProgressBar.setUI(LtuProgressBarDemoUi())
                            }
                        }
                        row("Repaint Interval (ms):") {
                            repaintIntervalSlider = slider(0, 200, 25, 50)
                                .bindValue(current::repaintInterval)
                                .component
                            repaintIntervalSlider.addChangeListener {
                                current.repaintInterval = repaintIntervalSlider.value
                                indeterminateExampleProgressBar.setUI(LtuProgressBarDemoUi())
                            }
                        }
                    }.visibleIf(advancedOptionsCheckBox.selected)
                }
            }
            group("Determinate") {
                panel {
                    row {
                        cell(determinateExampleProgressBar)
                    }
                }
            }
        }

        simulateProgress()

        return panel
    }

    private fun simulateProgress() {
        val totalTime = 2000L
        val intervalTime = 100L

        val increment = (determinateExampleProgressBar.maximum * intervalTime / totalTime).toInt()

        launch {
            while (isActive)
                for (i in 1..(totalTime / intervalTime)) {
                    delay(intervalTime)
                    determinateExampleProgressBar.value =
                        (determinateExampleProgressBar.value + increment) % determinateExampleProgressBar.maximum
                }
        }
    }

    override fun isModified(): Boolean {
        return current != initial
    }

    override fun reset() {
        current.copyFrom(initial)
        panel.reset()
        super.reset()
    }

    override fun cancel() {
        reset()
        super.cancel()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        initial.copyFrom(current)
        getConfig.loadState(initial)
    }

    override fun getDisplayName(): String {
        return "LTU Progress Bar"
    }

    override fun getId(): String {
        return "preferences.com.ltu.progressbar"
    }
}

