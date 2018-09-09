package com.young.roundcornerviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.young.roundcornerviews.utils.TargetCorner

/*
* Referenced : https://github.com/gavinliu/ShapedImageView
*/

private const val DEFAULT_STROKE_COLOR = 0x26000000
private const val DEFAULT_STROKE_WIDTH = 0f
internal const val DEFAULT_CORNER_RADIUS = 0f

class RoundCornerBorderView : View {

    private var strokeColor = DEFAULT_STROKE_COLOR
    private var strokeWidth = DEFAULT_STROKE_WIDTH

    private lateinit var strokeShape: Shape
    private lateinit var shape: Shape

    private var strokeBitmap:Bitmap? = null
    private var shapeBitmap:Bitmap? = null
    private var rectF = RectF()

    private val canvasShapeBitmap = Canvas()
    private val canvasStrokeBitmap = Canvas()

    private var strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.isFilterBitmap = true
        it.color = Color.BLACK
    }

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        color = Color.BLACK
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val paintBitmapShape = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private var paintBitmapStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = strokeColor
    }

    private val DST_OUT = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (isInEditMode) {
            return
        }
        var targetCorner = TargetCorner.NONE
        var cornerRadius = DEFAULT_CORNER_RADIUS
        attrs?.also {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RoundCornerBorderView)
            if (typedArray != null) {
                strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundCornerBorderView_strokeWidth, DEFAULT_STROKE_WIDTH.toInt()).toFloat()
                cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerBorderView_cornerRadius, DEFAULT_CORNER_RADIUS.toInt()).toFloat()
                strokeColor = typedArray.getColor(R.styleable.RoundCornerBorderView_strokeColor, DEFAULT_STROKE_COLOR)
                targetCorner = TargetCorner.fromOrdinal(typedArray.getInt(R.styleable.RoundCornerBorderView_targetCorner, TargetCorner.NONE.ordinal))
                typedArray.recycle()
            }
        }
        val radiusArray = targetCorner.getRadiusArray(cornerRadius)
        strokeShape = RoundRectShape(radiusArray, null, null)
        shape = RoundRectShape(radiusArray, null, null)

        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            val width = measuredWidth
            val height = measuredHeight

            shape.resize(width.toFloat(), height.toFloat())
            strokeShape.resize(width - strokeWidth * 2, height - strokeWidth * 2)

            makeStrokeBitmap()
            makeShapeBitmap()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (strokeWidth > 0) {
            var isStrokeBitmapMade = true
            if (strokeBitmap == null || strokeBitmap!!.isRecycled) {
                isStrokeBitmapMade = makeStrokeBitmap()
            }
            rectF.set(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat())
            val balance = saveLayerCompat(canvas, rectF, null)

            strokePaint.xfermode = null
            if (isStrokeBitmapMade) {
                canvas.drawBitmap(strokeBitmap, 0F, 0F, strokePaint)
            }
            canvas.translate(strokeWidth, strokeWidth)
            strokePaint.xfermode = DST_OUT
            strokeShape.draw(canvas, strokePaint)
            canvas.restoreToCount(balance)
        }
        var isShapeBitmapMade = true
        if (shapeBitmap == null || shapeBitmap!!.isRecycled) {
            isShapeBitmapMade =  makeShapeBitmap()
        }
        if (!isShapeBitmapMade) {
            return
        }
        canvas.drawBitmap(shapeBitmap, 0F, 0F, paint)
    }

    @Suppress("DEPRECATION")
    private fun saveLayerCompat(canvas: Canvas, rectF: RectF, paint: Paint?): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // This method was deprecated in API level 26 and not recommented since 22, but its
            // 2-parameter replacement is only available starting at API level 21.
            return canvas.saveLayer(rectF, paint, Canvas.ALL_SAVE_FLAG)
        } else {
            return canvas.saveLayer(rectF, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseBitmap(strokeBitmap)
        releaseBitmap(shapeBitmap)
    }

    private fun makeStrokeBitmap(): Boolean {
        if (strokeWidth <= 0) {
            return false
        }

        val width = measuredWidth
        val height = measuredHeight

        if (width == 0 || height == 0) {
            return false
        }

        releaseBitmap(strokeBitmap)

        strokeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvasStrokeBitmap.setBitmap(strokeBitmap)
        paintBitmapStroke.color = strokeColor
        canvasStrokeBitmap.drawRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), paintBitmapStroke)
        return true
    }

    private fun makeShapeBitmap(): Boolean {
        val width = measuredWidth
        val height = measuredHeight

        if (width == 0 || height == 0) {
            return false
        }

        releaseBitmap(shapeBitmap)

        shapeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvasShapeBitmap.setBitmap(shapeBitmap)
        shape.draw(canvasShapeBitmap, paintBitmapShape)
        return true
    }

    private fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}
