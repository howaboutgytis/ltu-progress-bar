package com.ltu.progressbar.ui

import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.UIUtilities
import com.ltu.progressbar.icon.IconVytis
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.Timer
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicProgressBarUI

open class LtuProgressBarUi : BasicProgressBarUI() {

    companion object {
        // to paint tricolor - 1/3 for each color
        private const val A_THIRD = 0.333f
    }

    private var animationTimer: Timer? = null

    private var coatOfArms: ImageIcon

    init {
        val image = BufferedImage(IconVytis.SMALL.iconWidth, IconVytis.SMALL.iconHeight, BufferedImage.TYPE_INT_ARGB)
        val graphic2d = image.createGraphics()
        IconVytis.SMALL.paintIcon(null, graphic2d, 0, 0)
        graphic2d.dispose()

        val scaledImage = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
        coatOfArms = ImageIcon(scaledImage)
    }

    override fun installDefaults() {
        super.installDefaults()
        UIManager.put("ProgressBar.cycleTime", 3200)
    }

    override fun installUI(c: JComponent) {
        super.installUI(c)
        // Value (in ms) to control the repaint speed
        animationTimer = Timer(55) {
            progressBar.repaint()
        }
        animationTimer?.start()
    }

    override fun uninstallUI(c: JComponent) {
        animationTimer?.stop()
        animationTimer = null
        super.uninstallUI(c)
    }

    override fun paintDeterminate(graphics: Graphics, component: JComponent) {
        val graphic2d = graphics as? Graphics2D ?: return

        if (isProgressBarInvalid()) return

        if (progressBar.orientation != SwingConstants.HORIZONTAL || !component.componentOrientation.isLeftToRight) {
            super.paintDeterminate(graphic2d, component)
            return
        }

        val barBorders = progressBar.insets
        val barWidth = progressBar.width
        var barHeight = progressBar.preferredSize.height
        if (((component.height - barHeight) and 1) != 0) barHeight++ // if not even

        val barRectangleWidth = barWidth - (barBorders.right + barBorders.left)
        val barRectangleHeight = barHeight - (barBorders.top + barBorders.bottom)

        if (component.isOpaque && component.parent != null) {
            graphic2d.color = component.parent.background
            graphic2d.fillRect(0, 0, barWidth, barHeight)
        }

        val off = JBUIScale.scale(1f)

        graphic2d.translate(0, (component.height - barHeight) / 2)
        graphic2d.color = progressBar.foreground
        graphic2d.fill(Rectangle2D.Float(0f, 0f, barWidth - off, barHeight - off))
        graphic2d.color = component.parent.background
        graphic2d.fill(Rectangle2D.Float(off, off, barWidth - 2f * off - off, barHeight - 2f * off - off))

        val baseTricolorPaint = getTricolor(barHeight)
        graphic2d.paint = baseTricolorPaint

        val amountFull = getAmountFull(barBorders, barRectangleWidth, barRectangleHeight)
        coatOfArms.paintIcon(progressBar, graphic2d, amountFull - JBUIScale.scale(3), 0)
        graphic2d.fill(
            Rectangle2D.Float(
                2f * off,
                2f * off,
                amountFull - JBUIScale.scale(5f),
                barHeight - JBUIScale.scale(5f)
            )
        )
        graphic2d.translate(0, -(component.height - barHeight) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(
                graphic2d, barBorders.left, barBorders.top,
                barRectangleWidth, barRectangleHeight,
                amountFull, barBorders
            )
        }
    }

    override fun paintIndeterminate(graphics: Graphics?, component: JComponent) {
        val graphic2d = graphics as? Graphics2D ?: return

        if (isProgressBarInvalid()) return

        val barBorders = progressBar.insets
        val barWidth = progressBar.width
        var barHeight = progressBar.preferredSize.height
        if (((component.height - barHeight) and 1) != 0) barHeight++ // if not even

        if (component.isOpaque && component.parent != null) {
            graphic2d.color = component.parent.background
            graphic2d.fillRect(0, (component.height - barHeight - (barBorders.top + barBorders.bottom)) / 2, barWidth, barHeight)
        }

        graphic2d.translate(0, (component.height - barHeight) / 2)

        val baseTricolorPaint = getTricolor(barHeight)
        graphic2d.paint = baseTricolorPaint
        graphic2d.fill(Rectangle2D.Float(1f, 1f, barWidth - 2f, barHeight - 2f))

        drawIndeterminateIcon(component, graphic2d)

        drawIndeterminateString(graphic2d)
    }

    private fun drawIndeterminateIcon(component: JComponent, graphic2d: Graphics2D) {
        boxRect = getBox(boxRect)
        val iconTopLocation = JBUIScale.scale(2)
        coatOfArms.paintIcon(component, graphic2d, boxRect.x, progressBar.insets.top - iconTopLocation)
    }

    private fun drawIndeterminateString(graphic2d: Graphics2D) {
        val rectangle = Rectangle(progressBar.size)
        if (progressBar.isStringPainted) {
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(graphic2d, progressBar.insets.left, progressBar.insets.top,
                    rectangle.width, rectangle.height, boxRect.x, boxRect.width)
            } else {
                paintString(graphic2d, progressBar.insets.left, progressBar.insets.top,
                    rectangle.width, rectangle.height, boxRect.y, boxRect.height)
            }
        }
    }

    private fun paintString(graphic2d: Graphics2D, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        val progressString = progressBar.string
        graphic2d.font = progressBar.font
        var renderLocation = getStringPlacement(graphic2d, progressString, x, y, w, h)
        val oldClip = graphic2d.clipBounds

        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
            graphic2d.color = selectionBackground
            UIUtilities.drawString(progressBar, graphic2d, progressString, renderLocation.x, renderLocation.y)

            graphic2d.color = selectionForeground
            graphic2d.clipRect(fillStart, y, amountFull, h)
            UIUtilities.drawString(progressBar, graphic2d, progressString, renderLocation.x, renderLocation.y)
        } else { // VERTICAL
            graphic2d.color = selectionBackground
            val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
            graphic2d.font = progressBar.font.deriveFont(rotate)
            renderLocation = getStringPlacement(graphic2d, progressString, x, y, w, h)
            UIUtilities.drawString(progressBar, graphic2d, progressString, renderLocation.x, renderLocation.y)

            graphic2d.color = selectionForeground
            graphic2d.clipRect(x, fillStart, w, amountFull)
            UIUtilities.drawString(progressBar, graphic2d, progressString, renderLocation.x, renderLocation.y)
        }
        graphic2d.clip = oldClip
    }

    private fun isProgressBarInvalid(): Boolean {
        return progressBar.width - (progressBar.insets.left + progressBar.insets.right) <= 0
                || progressBar.height - (progressBar.insets.top + progressBar.insets.bottom) <= 0
    }

    private fun getTricolor(barHeight: Int): LinearGradientPaint {
        val tricolor = arrayOf<Color>(Color.YELLOW, Color(30, 110, 0), Color(200, 0, 0))
        val baseTricolorPaint = LinearGradientPaint(
            0f, JBUIScale.scale(1).toFloat(), 0f, (barHeight - JBUIScale.scale(3)).toFloat(),
            floatArrayOf(A_THIRD * 1, A_THIRD * 2, A_THIRD * 3),
            tricolor
        )
        return baseTricolorPaint
    }

    /**
     * Override to correct icon draw for full bar width.
     */
    protected override fun getBox(r: Rectangle?): Rectangle {
        val rectangle = super.getBox(r) ?: Rectangle()

        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
            // Full width that the animation can traverse
            val availableWidth =
                progressBar.width - (progressBar.insets.left + progressBar.insets.right) - coatOfArms.iconWidth

            val frame = animationIndex
            val halfCycle = frameCount / 2

            val position = if (frame < halfCycle) {
                // Forward movement (0 to max)
                (animationIndex * availableWidth) / (halfCycle - 1)
            } else {
                // Backward movement (max to 0)
                availableWidth - ((frame - halfCycle) * availableWidth) / (frameCount - halfCycle - 1)
            }

            rectangle.x = position + progressBar.insets.left
            rectangle.width = availableWidth
        }
        return rectangle
    }
}
