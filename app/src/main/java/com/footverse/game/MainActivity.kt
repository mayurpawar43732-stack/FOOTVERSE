package com.footverse.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.max

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(FootverseView())
    }
}

private class FootverseView : View {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val teams = listOf("INDIA", "PORTUGAL", "ARGENTINA", "FRANCE", "BRAZIL", "GERMANY", "USA", "SPAIN", "ENGLAND", "JAPAN")
    private var teamIndex = 0
    private var opponentIndex = 1
    private var playing = false
    private var scoreHome = 0
    private var scoreAway = 0
    private var seconds = 90
    private var lastTick = System.currentTimeMillis()
    private var playerX = 0f
    private var playerY = 0f
    private var ballX = 0f
    private var ballY = 0f
    private var ballVx = 0f
    private var ballVy = 0f
    private var lastW = 0f
    private var lastH = 0f

    constructor() : super(null)

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return
        lastW = w
        lastH = h
        if (!playing) drawMenu(c, w, h) else drawMatch(c, w, h)
        postInvalidateDelayed(32)
    }

    private fun text(c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int) {
        p.color = color
        p.textSize = size
        p.typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
        p.textAlign = Paint.Align.CENTER
        c.drawText(s, x, y, p)
    }

    private fun drawMenu(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "FOOTVERSE", w / 2, h * .20f, h * .13f, Color.WHITE)
        text(c, "THE WORLD IS YOUR PITCH", w / 2, h * .28f, h * .035f, Color.rgb(70, 220, 160))
        text(c, "CHOOSE YOUR NATIONAL TEAM", w / 2, h * .42f, h * .055f, Color.LTGRAY)
        val bw = w * .17f
        val bh = h * .15f
        val left = w / 2 - bw / 2
        p.color = Color.rgb(20, 42, 57)
        c.drawRoundRect(RectF(left, h * .48f, left + bw, h * .48f + bh), 18f, 18f, p)
        text(c, teams[teamIndex], w / 2, h * .48f + bh * .62f, h * .07f, Color.WHITE)
        text(c, "TAP LEFT / RIGHT TO CHANGE TEAM", w / 2, h * .70f, h * .035f, Color.LTGRAY)
        p.color = Color.rgb(27, 190, 120)
        c.drawRoundRect(RectF(w * .37f, h * .78f, w * .63f, h * .92f), 22f, 22f, p)
        text(c, "KICK OFF", w / 2, h * .87f, h * .065f, Color.WHITE)
        text(c, "INDIA • PORTUGAL • ARGENTINA • FRANCE • BRAZIL • MORE", w / 2, h * .97f, h * .026f, Color.GRAY)
    }

    private fun startMatch() {
        opponentIndex = (teamIndex + 1) % teams.size
        scoreHome = 0
        scoreAway = 0
        seconds = 90
        playerX = lastW * .30f
        playerY = lastH * .52f
        ballX = lastW * .50f
        ballY = lastH * .50f
        ballVx = 0f
        ballVy = 0f
        lastTick = System.currentTimeMillis()
        playing = true
    }

    private fun drawMatch(c: Canvas, w: Float, h: Float) {
        val now = System.currentTimeMillis()
        if (now - lastTick >= 1000) {
            seconds = max(0, seconds - 1)
            lastTick = now
        }
        if (seconds == 0) {
            playing = false
            drawMenu(c, w, h)
            text(c, "FULL TIME  •  " + scoreHome + " - " + scoreAway, w / 2, h * .74f, h * .06f, Color.WHITE)
            return
        }
        c.drawColor(Color.rgb(5, 45, 28))
        p.color = Color.rgb(22, 126, 65)
        c.drawRect(0f, h * .08f, w, h * .94f, p)
        p.color = Color.rgb(28, 145, 74)
        for (i in 0..5) if (i % 2 == 0) c.drawRect(i * w / 6, h * .08f, (i + 1) * w / 6, h * .94f, p)
        p.color = Color.WHITE
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f
        c.drawRect(w * .04f, h * .10f, w * .96f, h * .92f, p)
        c.drawLine(w / 2, h * .10f, w / 2, h * .92f, p)
        c.drawCircle(w / 2, h * .51f, h * .13f, p)
        c.drawRect(w * .04f, h * .34f, w * .17f, h * .68f, p)
        c.drawRect(w * .83f, h * .34f, w * .96f, h * .68f, p)
        p.style = Paint.Style.FILL
        p.color = Color.WHITE
        c.drawRoundRect(RectF(w * .34f, 0f, w * .66f, h * .075f), 0f, 0f, p)
        text(c, teams[teamIndex] + "  " + scoreHome + " - " + scoreAway + "  " + teams[opponentIndex], w / 2, h * .055f, h * .045f, Color.BLACK)
        text(c, "00:" + seconds.toString().padStart(2, '0'), w * .92f, h * .055f, h * .035f, Color.WHITE)
        for (i in 0..4) {
            drawPlayer(c, w * (.12f + i * .045f), h * (.25f + i * .13f), Color.rgb(245, 205, 45))
            drawPlayer(c, w * (.70f + i * .045f), h * (.25f + i * .13f), Color.rgb(235, 65, 70))
        }
        drawPlayer(c, playerX, playerY, Color.rgb(255, 220, 50))
        ballX += ballVx
        ballY += ballVy
        ballVx *= .985f
        ballVy *= .985f
        if (ballY < h * .12f || ballY > h * .90f) ballVy *= -.8f
        if (ballX > w * .94f && ballY > h * .36f && ballY < h * .66f) {
            scoreHome++
            ballX = w * .5f; ballY = h * .5f; ballVx = 0f; ballVy = 0f
        }
        if (ballX < w * .06f && ballY > h * .36f && ballY < h * .66f) {
            scoreAway++
            ballX = w * .5f; ballY = h * .5f; ballVx = 0f; ballVy = 0f
        }
        ballX = ballX.coerceIn(w * .045f, w * .955f)
        ballY = ballY.coerceIn(h * .11f, h * .91f)
        p.color = Color.WHITE
        c.drawCircle(ballX, ballY, h * .018f, p)
        p.color = Color.argb(180, 20, 30, 42)
        c.drawCircle(w * .12f, h * .79f, h * .105f, p)
        text(c, "MOVE", w * .12f, h * .80f, h * .035f, Color.WHITE)
        p.color = Color.argb(220, 20, 160, 105)
        c.drawRoundRect(RectF(w * .80f, h * .72f, w * .96f, h * .88f), 22f, 22f, p)
        text(c, "SHOOT", w * .88f, h * .815f, h * .045f, Color.WHITE)
        text(c, "Tap the pitch to move • SHOOT to kick", w * .5f, h * .975f, h * .027f, Color.WHITE)
    }

    private fun drawPlayer(c: Canvas, x: Float, y: Float, color: Int) {
        p.color = color
        c.drawCircle(x, y, lastH * .025f, p)
        p.color = Color.rgb(25, 30, 35)
        c.drawCircle(x, y - lastH * .032f, lastH * .014f, p)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action != MotionEvent.ACTION_DOWN && e.action != MotionEvent.ACTION_MOVE) return true
        val x = e.x
        val y = e.y
        val w = width.toFloat()
        val h = height.toFloat()
        if (!playing) {
            if (y > h * .45f && y < h * .70f) {
                teamIndex = if (x < w / 2) (teamIndex - 1 + teams.size) % teams.size else (teamIndex + 1) % teams.size
            } else if (y > h * .76f) startMatch()
        } else {
            if (x > w * .78f && y > h * .68f) {
                val dx = w * .96f - ballX
                val dy = h * .51f - ballY
                val d = max(1f, hypot(dx, dy))
                ballVx = dx / d * w * .035f
                ballVy = dy / d * h * .035f
                ballX = playerX + lastW * .035f
                ballY = playerY
            } else {
                playerX = x.coerceIn(w * .08f, w * .78f)
                playerY = y.coerceIn(h * .16f, h * .88f)
                if (hypot(playerX - ballX, playerY - ballY) < h * .10f) {
                    ballX = playerX + w * .035f
                    ballY = playerY
                }
            }
        }
        invalidate()
        return true
    }
}
