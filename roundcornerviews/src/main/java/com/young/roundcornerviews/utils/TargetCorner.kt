package com.young.roundcornerviews.utils

enum class TargetCorner {
    // The corners are ordered top-left, top-right, bottom-right, bottom-left
    ALL {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        } },
    TOP {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(radius, radius, radius, radius, 0.0f, 0.0f, 0.0f, 0.0f)
        } },
    BOTTOM {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
        } },
    LEFT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(radius, radius, 0.0f, 0.0f, 0.0f, 0.0f, radius, radius)
        } },
    RIGHT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, radius, radius, radius, radius, 0.0f, 0.0f)
        } },
    TOP_LEFT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(radius, radius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        } },
    TOP_RIGHT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, radius, radius, 0.0f, 0.0f, 0.0f, 0.0f)
        } },
    BOTTOM_LEFT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, radius, radius)
        } },
    BOTTOM_RIGHT {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, 0.0f, 0.0f)
        } },
    NONE {
        override fun getRadiusArray(radius: Float): FloatArray {
            return floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        }
    };
    abstract fun getRadiusArray(radius: Float): FloatArray

    companion object {
        fun fromOrdinal(ordinal: Int): TargetCorner {
            for (targetCorner in TargetCorner.values()) {
                if (ordinal == targetCorner.ordinal) {
                    return targetCorner
                }
            }
            return NONE
        }
    }
}