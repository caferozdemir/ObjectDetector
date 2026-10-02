package com.example.objectdetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.objects.DetectedObject
import kotlin.math.max

class ObjectGraphic(
    overlay: GraphicOverlay,
    private val detectedObject: DetectedObject,
    private val imageRect: android.graphics.Rect,
    private val isImageFlipped: Boolean
) : GraphicOverlay.Graphic(overlay) {

    private val boxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8.0f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50.0f
        setShadowLayer(5.0f, 0f, 0f, Color.BLACK)
    }

    private val labelPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        // Calculate scale factors
        val scaleX = canvas.width.toFloat() / imageRect.height().toFloat()
        val scaleY = canvas.height.toFloat() / imageRect.width().toFloat()
        val scale = max(scaleX, scaleY)

        val offsetX = (canvas.width - imageRect.height() * scale) / 2
        val offsetY = (canvas.height - imageRect.width() * scale) / 2

        val boundingBox = detectedObject.boundingBox
        val rect = RectF(
            boundingBox.left * scale + offsetX,
            boundingBox.top * scale + offsetY,
            boundingBox.right * scale + offsetX,
            boundingBox.bottom * scale + offsetY
        )

        canvas.drawRect(rect, boxPaint)

        // Draw labels
        var yPosition = rect.top - 10
        for (label in detectedObject.labels) {
            val text = "${label.text} (%.2f)".format(label.confidence)
            val textWidth = textPaint.measureText(text)
            
            // Draw background for text
            canvas.drawRect(
                rect.left,
                yPosition - textPaint.textSize,
                rect.left + textWidth + 10,
                yPosition + 10,
                labelPaint
            )
            
            canvas.drawText(text, rect.left + 5, yPosition, textPaint)
            yPosition -= (textPaint.textSize + 10)
        }
    }
}
