package com.ltu.progressbar.settings

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(name = "LtuProgressBarPersistentConfigs", storages = [Storage("LtuProgressBarPersistentConfigs.xml")])
class LtuProgressBarPersistentConfigsComponent : SimplePersistentStateComponent<LtuProgressBarPersistentConfigs>(LtuProgressBarPersistentConfigs())