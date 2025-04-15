package com.ltu.progressbar.ui

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.UIUtilities
import com.ltu.progressbar.icon.IconVytis
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.concurrent.Volatile

open class LtuProgressBarUi : BasicProgressBarUI() {

    @Volatile
    private var offset = 0

    @Volatile
    private var offset2 = 0

    @Volatile
    private var velocity = 1

    companion object {
        private const val A_THIRD = 0.333f
    }

    override fun paintDeterminate(g: Graphics, c: JComponent) {
        if (g !is Graphics2D) {
            return
        }

        if (progressBar.orientation != SwingConstants.HORIZONTAL || !c.componentOrientation.isLeftToRight) {
            super.paintDeterminate(g, c)
            return
        }
        val config = GraphicsUtil.setupAAPainting(g)
        val b = progressBar.insets // area for border
        val w = progressBar.width
        var h = progressBar.preferredSize.height
        if (((c.height - h) and 1) != 0) h++ // if not even

        val barRectWidth = w - (b.right + b.left)
        val barRectHeight = h - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }

        val amountFull = getAmountFull(b, barRectWidth, barRectHeight)

        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()

        g.setColor(background)
        if (c.isOpaque) {
            g.fillRect(0, 0, w, h)
        }

        val off = JBUIScale.scale(1f)

        g.translate(0, (c.height - h) / 2)
        g.color = progressBar.foreground
        g.fill(Rectangle2D.Float(0f, 0f, w - off, h - off))
        g.color = background
        g.fill(Rectangle2D.Float(off, off, w - 2f * off - off, h - 2f * off - off))
        g.paint = LinearGradientPaint(
            0f, JBUIScale.scale(1).toFloat(), 0f, (h - JBUIScale.scale(3)).toFloat(),
            floatArrayOf(A_THIRD * 1, A_THIRD * 2, A_THIRD * 3),
            arrayOf<Color>(Color.YELLOW, Color(30, 110, 0), Color(200, 0, 0))
        )

        IconVytis.SMALL.paintIcon(progressBar, g, amountFull - JBUIScale.scale(3), 0)
        g.fill(
            Rectangle2D.Float(
                2f * off,
                2f * off,
                amountFull - JBUIScale.scale(5f),
                h - JBUIScale.scale(5f)
            )
        )
        g.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(
                g, b.left, b.top,
                barRectWidth, barRectHeight,
                amountFull, b
            )
        }
        config.restore()
    }

    override fun paintIndeterminate(g2d: Graphics?, c: JComponent) {
        if (g2d !is Graphics2D) {
            return
        }

        val b = progressBar.insets // area for border
        val barRectWidth = progressBar.width - (b.right + b.left)
        val barRectHeight = progressBar.height - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }
        g2d.color = JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50))
        val w = c.width
        var h = c.preferredSize.height
        if (((c.height - h) and 1) != 0) h++

        val baseTricolorPaint = LinearGradientPaint(
            0f, JBUIScale.scale(1).toFloat(), 0f, (h - JBUIScale.scale(3)).toFloat(),
            floatArrayOf(A_THIRD * 1, A_THIRD * 2, A_THIRD * 3),
            arrayOf<Color>(Color.YELLOW, Color(30, 110, 0), Color(200, 0, 0))
        )

        g2d.paint = baseTricolorPaint

        if (c.isOpaque) {
            g2d.fillRect(0, (c.height - h) / 2, w, h)
        }
        g2d.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        val config = GraphicsUtil.setupAAPainting(g2d)
        g2d.translate(0, (c.height - h) / 2)

        val old = g2d.paint
        g2d.paint = baseTricolorPaint

        val containingRect = Area(Rectangle2D.Float(1f, 1f, w - 2f, h - 2f))

        g2d.fill(containingRect)
        g2d.paint = old
        offset = (offset + 1) % getPeriodLength()
        offset2 += velocity
        if (offset2 <= 2) {
            offset2 = 2
            velocity = 1
        } else if (offset2 >= w - JBUIScale.scale(15)) {
            offset2 = w - JBUIScale.scale(15)
            velocity = -1
        }
        val area = Area(Rectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat()))
        area.subtract(Area(Rectangle2D.Float(1f, 1f, w - 2f, h - 2f)))
        g2d.paint = Gray._128
        if (c.isOpaque) {
            g2d.fill(area)
        }

        area.subtract(Area(Rectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat())))

        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()
        g2d.paint = background
        if (c.isOpaque) {
            g2d.fill(area)
        }

        IconVytis.SMALL.paintIcon(progressBar, g2d, offset2 - JBUIScale.scale(10), -JBUIScale.scale(6))

        g2d.draw(Rectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 1f))
        g2d.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(g2d, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(g2d, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
        config.restore()
    }

    private fun paintString(g: Graphics2D, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        val progressString = progressBar.string
        g.font = progressBar.font
        var renderLocation = getStringPlacement(g, progressString, x, y, w, h)
        val oldClip = g.clipBounds

        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
            g.color = selectionBackground
            UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)

            g.color = selectionForeground
            g.clipRect(fillStart, y, amountFull, h)
            UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)
        } else { // VERTICAL
            g.color = selectionBackground
            val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
            g.font = progressBar.font.deriveFont(rotate)
            renderLocation = getStringPlacement(g, progressString, x, y, w, h)
            UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)

            g.color = selectionForeground
            g.clipRect(x, fillStart, w, amountFull)
            UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)
        }
        g.clip = oldClip
    }

    private fun getPeriodLength(): Int {
        return JBUIScale.scale(16)
    }

}
