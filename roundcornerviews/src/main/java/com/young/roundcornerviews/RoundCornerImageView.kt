package com.young.roundcornerviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import com.young.roundcornerviews.utils.TargetCorner

/*
* Referenced : https://github.com/gavinliu/ShapedImageView
*/

class RoundCornerImageView : AppCompatImageView {

    private lateinit var shape: Shape
    
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        color = Color.BLACK
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val paintBitmap = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private var shapeBitmap:Bitmap? = null

    private val canvasShapeBitmap = Canvas()

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
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RoundCornerImageView)
            if (typedArray != null) {
                cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerImageView_cornerRadius, DEFAULT_CORNER_RADIUS.toInt()).toFloat()
                targetCorner = TargetCorner.fromOrdinal(typedArray.getInt(R.styleable.RoundCornerImageView_targetCorner, TargetCorner.NONE.ordinal))
                typedArray.recycle()
            }
        }
        val radiusArray = targetCorner.getRadiusArray(cornerRadius)
        shape = RoundRectShape(radiusArray, null, null)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            shape.resize(measuredWidth.toFloat(), measuredHeight.toFloat())

            makeShapeBitmap()
        }
    }

    private fun makeShapeBitmap(): Boolean {
        val width = measuredWidth
        val height = measuredHeight

        if (width == 0 || height == 0) {
            return false
        }

        releaseBitmap(shapeBitmap)

        if (shapeBitmap == null || shapeBitmap!!.isRecycled) {
            shapeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        canvasShapeBitmap.setBitmap(shapeBitmap)
        shape.draw(canvasShapeBitmap, paintBitmap)
        return true
    }

    private fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseBitmap(shapeBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var isBitmapMade = true
        if (shapeBitmap == null || shapeBitmap!!.isRecycled) {
            isBitmapMade = makeShapeBitmap()
        }
        if (!isBitmapMade) {
            return
        }
        canvas.drawBitmap(shapeBitmap, 0F, 0F, paint)
    }
}
