package com.ltu.progressbar.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * A java class is needed to override <code>createUI</code> static method.
 * The method is an entrypoint to swap default bar with a custom one.
 */
public class LtuProgressBarUiExtensionPoint extends LtuProgressBarUi {

    public static @NotNull ComponentUI createUI(JComponent c) {
        return new LtuProgressBarUi();
    }
}
