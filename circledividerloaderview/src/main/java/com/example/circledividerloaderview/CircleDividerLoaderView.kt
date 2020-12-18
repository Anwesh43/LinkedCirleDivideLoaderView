package com.example.circledividerloaderview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val parts : Int = 2
val lines : Int = 6
val scGap : Float = 0.02f / lines
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val deg : Float = 360f
val colors : Array<Int> = arrayOf(
    "#F44336",
    "#3F51B5",
    "#2196F3",
    "#FF9800",
    "#795548"
).map {
    Color.parseColor(it)
}.toTypedArray()

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawCircleDividerLoader(scale : Float, w : Float, h : Float, paint : Paint) {
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val size : Float = Math.min(w, h) / sizeFactor
    save()
    translate(w / 2, h / 2)
    save()
    translate(0f, -h / 4 - size)
    drawArc(RectF(-size, -size, size, size), 360f * sc2, 360f * (sc1 - sc2), false, paint)
    restore()
    save()
    translate(0f, h / 4 - size)
    for (j in 0..(lines - 1)) {
        val sc1k : Float = sc1.divideScale(j, lines)
        val sc2k : Float = sc2.divideScale(j, lines)
        save()
        rotate((deg / lines) * j)
        drawLine(size * sc2k, 0f, size * sc1k, 0f, paint)
        restore()
    }
    restore()
    restore()
}

fun Canvas.drawCDLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawCircleDividerLoader(scale, w, h, paint)
}

class CircleDividerLoaderView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}