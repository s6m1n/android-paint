package woowacourse.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

class PaintCanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var selectedColorInt = ContextCompat.getColor(context, default_color)
    private var selectedStrokeWidth = DEFAULT_STROKE_WIDTH
    private var path = Path()
    private var paint = createPaintWith(selectedColorInt, selectedStrokeWidth)
    private val canvasData = mutableListOf(path to paint)

    private var selectedDiagram = Diagram.PEN
    private var startX = 0F
    private var startY = 0F
    private var rect = RectF()
    private var oval = RectF()
    private var rectPaint = createDiagramPaintWith(selectedColorInt)
    private var ovalPaint = createDiagramPaintWith(selectedColorInt)
    private val canvasRectData = mutableListOf(rect to rectPaint)
    private val canvasOvalData = mutableListOf(oval to ovalPaint)

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun selectColorInt(
        @ColorInt value: Int,
    ) {
        selectedColorInt = value
    }

    fun selectDiagram(diagram: Diagram) {
        selectedDiagram = diagram
    }

    fun selectStrokeWidth(strokeWidth: Float) {
        selectedStrokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasData.forEach {
            canvas.drawPath(it.first, it.second)
        }
        canvasRectData.forEach {
            canvas.drawRect(it.first, it.second)
        }
        canvasOvalData.forEach {
            canvas.drawOval(it.first, it.second)
        }
        canvas.drawRect(rect, rectPaint)
        canvas.drawOval(oval, ovalPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startDrawing(pointX, pointY)
            MotionEvent.ACTION_MOVE -> progressDrawing(pointX, pointY)
            MotionEvent.ACTION_UP -> finishDrawing(pointX, pointY)
            else -> super.onTouchEvent(event)
        }
        invalidate()
        return true
    }

    private fun startDrawing(
        pointX: Float,
        pointY: Float,
    ) {
        when (selectedDiagram) {
            Diagram.PEN -> {
                startLine(pointX, pointY)
            }

            Diagram.RECT -> {
                startRect(pointX, pointY)
            }

            Diagram.OVAL -> {
                startOval(pointX, pointY)
            }
        }
    }

    private fun startLine(
        pointX: Float,
        pointY: Float,
    ) {
        path = Path().apply { moveTo(pointX, pointY) }
        canvasData.add(path to createPaintWith(selectedColorInt, selectedStrokeWidth))
    }

    private fun startRect(
        pointX: Float,
        pointY: Float,
    ) {
        startX = pointX
        startY = pointY
        rectPaint = createDiagramPaintWith(selectedColorInt)
    }

    private fun startOval(
        pointX: Float,
        pointY: Float,
    ) {
        startX = pointX
        startY = pointY
        ovalPaint = createDiagramPaintWith(selectedColorInt)
    }

    private fun progressDrawing(
        pointX: Float,
        pointY: Float,
    ) {
        when (selectedDiagram) {
            Diagram.PEN -> {
                progressLine(pointX, pointY)
            }

            Diagram.RECT -> {
                rect = RectF(startX, startY, pointX, pointY)
            }

            Diagram.OVAL -> {
                oval = RectF(startX, startY, pointX, pointY)
            }
        }
    }

    private fun progressLine(
        pointX: Float,
        pointY: Float,
    ) {
        path.lineTo(pointX, pointY)
    }

    private fun finishDrawing(
        pointX: Float,
        pointY: Float,
    ) {
        if (selectedDiagram == Diagram.RECT) {
            rect = RectF(startX, startY, pointX, pointY)
            canvasRectData.add(rect to rectPaint)
        } else if (selectedDiagram == Diagram.OVAL) {
            oval = RectF(startX, startY, pointX, pointY)
            canvasOvalData.add(oval to ovalPaint)
        }
    }

    private fun createPaintWith(
        colorValue: Int,
        width: Float,
    ) = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = colorValue
        strokeWidth = width
    }

    private fun createDiagramPaintWith(colorValue: Int) =
        Paint().apply {
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            style = Paint.Style.FILL
            color = colorValue
        }

    companion object {
        private val default_color = R.color.red
        private const val DEFAULT_STROKE_WIDTH = 10F
    }
}
