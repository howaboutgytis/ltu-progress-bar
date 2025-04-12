package com.ltu.progressbar

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceListener
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

@Service(Service.Level.APP)
class LtuProgressBarService {
    // Service implementation will go here
}

class LtuProgressBarStartupActivity : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        // Initialize plugin components on project open
        val progressBarService = ApplicationManager.getApplication().service<LtuProgressBarService>()
        // Additional initialization logic
    }
}
