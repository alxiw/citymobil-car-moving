package io.github.alxiw.citymobilcarmoving

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import kotlin.math.abs
import kotlin.math.atan2

class DriveLayout : FrameLayout {

    private lateinit var car: CarView
    private var isCarMove: Boolean = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val car = CarView(context)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        car.layoutParams = params

        this.car = car
        addView(car)

        setOnTouchListener { _, event ->
            if (car != null && event.action == MotionEvent.ACTION_UP) {
                if (!isCarMove) {
                    moveCar(Point(event.x.toInt(), event.y.toInt()))
                    isCarMove = true
                }
            }
            true
        }
    }

    private fun moveCar(newCarPosition: Point) {
        val currentCarPosition = Point((car.x + car.width / 2).toInt(), (car.y + car.height / 2).toInt())

        var currentCarRadians = Math.toRadians(car.rotation.toDouble())
        var newCarRadians = atan2((newCarPosition.x - currentCarPosition.x).toDouble(), (-(newCarPosition.y - currentCarPosition.y)).toDouble())

        if (abs(currentCarRadians - newCarRadians) > Math.PI) {
            when {
                newCarRadians < 0 -> newCarRadians += Math.PI.toFloat() * 2
                currentCarRadians < 0 -> currentCarRadians += Math.PI.toFloat() * 2
                currentCarRadians > Math.PI -> currentCarRadians -= Math.PI.toFloat() * 2
            }
        }

        val currentCarRotation = Math.toDegrees(currentCarRadians).toFloat()
        val newCarRotation = Math.toDegrees(newCarRadians).toFloat()

        val rotate = ObjectAnimator.ofFloat(car, "rotation", currentCarRotation, newCarRotation)
            .also {
                it.interpolator = LinearInterpolator()
                it.duration = Math.toDegrees(abs(newCarRotation - currentCarRotation).toDouble() / 8.0f).toLong()
            }

        val path = Path()
        path.setLastPoint(
            (currentCarPosition.x - car.width / 2).toFloat(),
            (currentCarPosition.y - car.height / 2).toFloat()
        )
        path.lineTo(
            (newCarPosition.x - car.width / 2).toFloat(),
            (newCarPosition.y - car.height / 2).toFloat()
        )
        invalidate()

        val move = ObjectAnimator.ofFloat(car, X, Y, path)
            .also {
                it.interpolator = AccelerateDecelerateInterpolator()
                it.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        isCarMove = false
                    }
                })
                it.duration = (PathMeasure(path, false).length / 2.0f).toLong()
            }

        AnimatorSet().also {
            it.playSequentially(rotate, move)
            it.start()
        }
    }

    inner class CarView : ImageView {

        private lateinit var icon: Drawable

        constructor(context: Context): super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
            init()
        }

        private fun init() {
            icon = resources.getDrawable(R.drawable.ic_car, null)
            setImageDrawable(icon)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(icon.intrinsicHeight / 10, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(icon.intrinsicWidth / 10, MeasureSpec.EXACTLY)
            )
        }

    }

}