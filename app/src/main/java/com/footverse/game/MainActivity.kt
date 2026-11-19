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
import kotlin.math.min
import kotlin.random.Random

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(FootverseView(this))
    }
}

private class FootverseView(context: android.content.Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val teams = listOf("INDIA", "PORTUGAL", "ARGENTINA", "FRANCE", "BRAZIL", "GERMANY", "USA", "SPAIN", "ENGLAND", "JAPAN", "NETHERLANDS", "ITALY", "CROATIA", "MOROCCO")
    private val rosters = mapOf(
        "INDIA" to listOf("Sunil Chhetri", "Gurpreet Singh", "Sandesh Jhingan", "Anirudh Thapa", "Lallianzuala Chhangte", "Brandon Fernandes", "Rahul Bheke", "Sahal Abdul Samad", "Liston Colaco", "Akash Mishra", "Udanta Singh"),
        "PORTUGAL" to listOf("Cristiano Ronaldo", "Bruno Fernandes", "Bernardo Silva", "Rafael Leao", "Diogo Jota", "Joao Cancelo", "Ruben Dias", "Joao Felix", "Vitinha", "Pepe", "Diogo Costa"),
        "ARGENTINA" to listOf("Lionel Messi", "Lautaro Martinez", "Angel Di Maria", "Julian Alvarez", "Enzo Fernandez", "Alexis Mac Allister", "Rodrigo De Paul", "Nicolas Otamendi", "Emiliano Martinez", "Paulo Dybala", "Lisandro Martinez"),
        "FRANCE" to listOf("Kylian Mbappe", "Antoine Griezmann", "Ousmane Dembele", "Olivier Giroud", "Aurelien Tchouameni", "Eduardo Camavinga", "N'Golo Kante", "William Saliba", "Theo Hernandez", "Mike Maignan", "Jules Kounde"),
        "BRAZIL" to listOf("Neymar Jr", "Vinicius Jr", "Rodrygo", "Raphinha", "Casemiro", "Bruno Guimaraes", "Marquinhos", "Alisson", "Endrick", "Gabriel Martinelli", "Thiago Silva"),
        "GERMANY" to listOf("Jamal Musiala", "Florian Wirtz", "Kai Havertz", "Ilkay Gundogan", "Joshua Kimmich", "Leroy Sane", "Antonio Rudiger", "Niclas Fullkrug", "Manuel Neuer", "Toni Kroos", "Thomas Muller"),
        "USA" to listOf("Christian Pulisic", "Weston McKennie", "Tim Weah", "Giovanni Reyna", "Tyler Adams", "Folarin Balogun", "Sergino Dest", "Antonee Robinson", "Matt Turner", "Yunus Musah", "Ricardo Pepi"),
        "SPAIN" to listOf("Lamine Yamal", "Pedri", "Gavi", "Rodri", "Nico Williams", "Alvaro Morata", "Dani Olmo", "Ferran Torres", "Aymeric Laporte", "Dani Carvajal", "Unai Simon"),
        "ENGLAND" to listOf("Harry Kane", "Jude Bellingham", "Bukayo Saka", "Phil Foden", "Declan Rice", "Cole Palmer", "Marcus Rashford", "John Stones", "Kyle Walker", "Jordan Pickford", "Trent Alexander-Arnold"),
        "JAPAN" to listOf("Takefusa Kubo", "Kaoru Mitoma", "Wataru Endo", "Daichi Kamada", "Takumi Minamino", "Ritsu Doan", "Ao Tanaka", "Hiroki Ito", "Takehiro Tomiyasu", "Zion Suzuki", "Junya Ito"),
        "NETHERLANDS" to listOf("Virgil van Dijk", "Memphis Depay", "Cody Gakpo", "Frenkie de Jong", "Xavi Simons", "Denzel Dumfries", "Nathan Ake", "Micky van de Ven", "Jeremie Frimpong", "Tijjani Reijnders", "Bart Verbruggen"),
        "ITALY" to listOf("Gianluigi Donnarumma", "Federico Chiesa", "Nicolo Barella", "Jorginho", "Lorenzo Pellegrini", "Alessandro Bastoni", "Giacomo Raspadori", "Ciro Immobile", "Domenico Berardi", "Andrea Cambiaso", "Guglielmo Vicario"),
        "CROATIA" to listOf("Luka Modric", "Joško Gvardiol", "Ivan Perisic", "Mateo Kovacic", "Marcelo Brozovic", "Andrej Kramaric", "Josip Stanisic", "Bruno Petkovic", "Dominik Livakovic", "Mario Pasalic", "Ante Budimir"),
        "MOROCCO" to listOf("Achraf Hakimi", "Hakim Ziyech", "Youssef En-Nesyri", "Sofyan Amrabat", "Brahim Diaz", "Noussair Mazraoui", "Amine Harit", "Azzedine Ounahi", "Nayef Aguerd", "Abde Ezzalzouli", "Yassine Bounou")
    )
    private val formations = listOf("4-3-3", "4-4-2", "3-5-2", "4-2-3-1")
    private var formationIndex = 0
    private var teamIndex = 0
    private var opponentIndex = 1
    private var selectedPlayers = mutableSetOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    private var screen = 0 // 0 home, 1 squad, 2 formation, 3 tournament, 4 match, 5 result
    private var scoreHome = 0
    private var scoreAway = 0
    private var seconds = 120
    private var lastTick = System.currentTimeMillis()
    private var playerX = 0f
    private var playerY = 0f
    private var ballX = 0f
    private var ballY = 0f
    private var ballVx = 0f
    private var ballVy = 0f
    private var aiX = 0f
    private var aiY = 0f
    private var lastAi = 0L
    private var lastW = 0f
    private var lastH = 0f
    private var tournamentRound = 1
    private var tournamentWins = 0
    private var matchNumber = 1
    private var lastGoal = 0L
    private var notice = "Build your dream national team"
    private val playerNames get() = rosters[teams[teamIndex]] ?: rosters["INDIA"]!!
    private val opponentNames get() = rosters[teams[opponentIndex]] ?: rosters["PORTUGAL"]!!

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        lastW = w; lastH = h
        when (screen) {
            0 -> drawHome(c, w, h)
            1 -> drawSquad(c, w, h)
            2 -> drawFormation(c, w, h)
            3 -> drawTournament(c, w, h)
            4 -> drawMatch(c, w, h)
            else -> drawResult(c, w, h)
        }
        postInvalidateDelayed(if (screen == 4) 24 else 80)
    }

    private fun text(c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int, align: Paint.Align = Paint.Align.CENTER) {
        p.color = color; p.textSize = size
        p.typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
        p.textAlign = align
        c.drawText(s, x, y, p)
    }

    private fun panel(c: Canvas, l: Float, t: Float, r: Float, b: Float, color: Int = Color.rgb(20, 42, 57)) {
        p.color = color
        c.drawRoundRect(RectF(l, t, r, b), 18f, 18f, p)
    }

    private fun button(c: Canvas, label: String, l: Float, t: Float, r: Float, b: Float, color: Int = Color.rgb(27, 190, 120)) {
        panel(c, l, t, r, b, color)
        text(c, label, (l + r) / 2, (t + b) / 2 + (b - t) * .16f, (b - t) * .38f, Color.WHITE)
    }

    private fun drawHome(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "FOOTVERSE", w / 2, h * .16f, h * .14f, Color.WHITE)
        text(c, "THE WORLD IS YOUR PITCH", w / 2, h * .24f, h * .035f, Color.rgb(70, 220, 160))
        text(c, "YOUR NATIONAL TEAM", w / 2, h * .34f, h * .045f, Color.LTGRAY)
        button(c, "‹", w * .28f, h * .39f, w * .36f, h * .53f, Color.rgb(30, 60, 78))
        panel(c, w * .38f, h * .39f, w * .62f, h * .53f)
        text(c, teams[teamIndex], w / 2, h * .475f, h * .07f, Color.WHITE)
        button(c, "›", w * .64f, h * .39f, w * .72f, h * .53f, Color.rgb(30, 60, 78))
        button(c, "SQUAD BUILDER", w * .12f, h * .60f, w * .39f, h * .74f, Color.rgb(31, 91, 125))
        button(c, "FORMATIONS", w * .42f, h * .60f, w * .63f, h * .74f, Color.rgb(31, 91, 125))
        button(c, "TOURNAMENT", w * .66f, h * .60f, w * .88f, h * .74f, Color.rgb(122, 83, 28))
        button(c, "KICK OFF", w * .35f, h * .80f, w * .65f, h * .94f)
        text(c, "14 NATIONAL TEAMS  •  CREATE YOUR DREAM XI", w / 2, h * .985f, h * .025f, Color.GRAY)
    }

    private fun drawSquad(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "SQUAD BUILDER", w / 2, h * .12f, h * .10f, Color.WHITE)
        text(c, teams[teamIndex] + "  •  SELECT 11 PLAYERS (" + selectedPlayers.size + "/11)", w / 2, h * .20f, h * .04f, Color.rgb(70, 220, 160))
        val list = playerNames
        for (i in list.indices) {
            val col = i % 6
            val row = i / 6
            val l = w * (.035f + col * .155f)
            val t = h * (.29f + row * .23f)
            val chosen = selectedPlayers.contains(i)
            panel(c, l, t, l + w * .14f, t + h * .17f, if (chosen) Color.rgb(25, 115, 77) else Color.rgb(27, 43, 59))
            text(c, (i + 1).toString() + ". " + list[i], l + w * .07f, t + h * .07f, h * .028f, Color.WHITE)
            text(c, if (chosen) "IN STARTING XI" else "TAP TO ADD", l + w * .07f, t + h * .13f, h * .023f, if (chosen) Color.rgb(130, 255, 185) else Color.LTGRAY)
        }
        button(c, "BACK", w * .08f, h * .82f, w * .25f, h * .94f, Color.rgb(60, 72, 88))
        button(c, "AUTO PICK BEST XI", w * .34f, h * .82f, w * .66f, h * .94f)
        button(c, "DONE", w * .75f, h * .82f, w * .92f, h * .94f, Color.rgb(31, 91, 125))
    }

    private fun drawFormation(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "CHOOSE FORMATION", w / 2, h * .14f, h * .10f, Color.WHITE)
        text(c, "CURRENT: " + formations[formationIndex], w / 2, h * .25f, h * .055f, Color.rgb(70, 220, 160))
        val names = listOf("4-3-3  •  ATTACKING", "4-4-2  •  BALANCED", "3-5-2  •  MIDFIELD", "4-2-3-1  •  CONTROL")
        for (i in formations.indices) {
            val y = h * (.34f + i * .12f)
            button(c, names[i], w * .25f, y, w * .75f, y + h * .09f, if (i == formationIndex) Color.rgb(25, 115, 77) else Color.rgb(31, 65, 85))
        }
        button(c, "BACK", w * .08f, h * .84f, w * .28f, h * .95f, Color.rgb(60, 72, 88))
        button(c, "SAVE FORMATION", w * .62f, h * .84f, w * .92f, h * .95f)
    }

    private fun drawTournament(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "FOOTVERSE CUP", w / 2, h * .16f, h * .12f, Color.WHITE)
        text(c, "YOUR TEAM: " + teams[teamIndex], w / 2, h * .30f, h * .05f, Color.rgb(70, 220, 160))
        text(c, "ROUND " + tournamentRound + " OF 4", w / 2, h * .43f, h * .07f, Color.WHITE)
        text(c, "WINS: " + tournamentWins + "     MATCH: " + matchNumber, w / 2, h * .53f, h * .045f, Color.LTGRAY)
        text(c, "Win matches to reach the final and lift the cup.", w / 2, h * .64f, h * .035f, Color.LTGRAY)
        button(c, "START CUP MATCH", w * .34f, h * .74f, w * .66f, h * .88f)
        button(c, "BACK", w * .06f, h * .84f, w * .22f, h * .95f, Color.rgb(60, 72, 88))
    }

    private fun startMatch(tournament: Boolean = false) {
        opponentIndex = (teamIndex + if (tournament) (tournamentRound + 2) else 1) % teams.size
        if (opponentIndex == teamIndex) opponentIndex = (opponentIndex + 1) % teams.size
        scoreHome = 0; scoreAway = 0; seconds = if (tournament) 150 else 120
        playerX = lastW * .30f; playerY = lastH * .52f
        aiX = lastW * .72f; aiY = lastH * .50f
        ballX = lastW * .50f; ballY = lastH * .50f
        ballVx = 0f; ballVy = 0f
        lastTick = System.currentTimeMillis(); lastAi = lastTick
        screen = 4
    }

    private fun drawPitch(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(5, 45, 28))
        p.color = Color.rgb(22, 126, 65); c.drawRect(0f, h * .07f, w, h * .94f, p)
        for (i in 0..7) if (i % 2 == 0) {
            p.color = Color.rgb(28, 145, 74)
            c.drawRect(i * w / 8, h * .07f, (i + 1) * w / 8, h * .94f, p)
        }
        p.color = Color.WHITE; p.style = Paint.Style.STROKE; p.strokeWidth = 3f
        c.drawRect(w * .04f, h * .09f, w * .96f, h * .92f, p)
        c.drawLine(w / 2, h * .09f, w / 2, h * .92f, p)
        c.drawCircle(w / 2, h * .505f, h * .13f, p)
        c.drawRect(w * .04f, h * .33f, w * .17f, h * .68f, p)
        c.drawRect(w * .83f, h * .33f, w * .96f, h * .68f, p)
        c.drawRect(w * .04f, h * .42f, w * .10f, h * .59f, p)
        c.drawRect(w * .90f, h * .42f, w * .96f, h * .59f, p)
        p.style = Paint.Style.FILL
    }

    private fun drawMatch(c: Canvas, w: Float, h: Float) {
        val now = System.currentTimeMillis()
        if (now - lastTick >= 1000) { seconds = max(0, seconds - 1); lastTick = now }
        if (seconds <= 0) {
            screen = 5
            if (scoreHome > scoreAway && tournamentRound < 4) { tournamentWins++; tournamentRound++ }
            else if (scoreHome > scoreAway) tournamentWins++
            return
        }
        drawPitch(c, w, h)
        // Formation-aware support players
        val support = when (formationIndex) {
            1 -> listOf(.18f to .30f, .28f to .43f, .28f to .58f, .18f to .72f, .42f to .28f, .42f to .72f)
            2 -> listOf(.20f to .24f, .20f to .50f, .20f to .76f, .40f to .20f, .40f to .40f, .40f to .60f, .40f to .80f)
            3 -> listOf(.17f to .30f, .17f to .70f, .37f to .25f, .37f to .50f, .37f to .75f, .48f to .50f)
            else -> listOf(.20f to .25f, .20f to .50f, .20f to .75f, .38f to .22f, .38f to .50f, .38f to .78f)
        }
        for ((x, y) in support) drawPlayer(c, w * x, h * y, Color.rgb(245, 205, 45), now)
        for (i in 0..6) {
            val targetX = w * (.62f + (i % 3) * .09f)
            val targetY = h * (.24f + (i / 3) * .23f)
            drawPlayer(c, targetX, targetY, Color.rgb(235, 65, 70), now)
        }
        // Opponent AI tracks the ball, closes space and shoots when close
        if (now - lastAi > 42) {
            val targetX = max(w * .55f, ballX - w * .025f)
            val targetY = ballY.coerceIn(h * .17f, h * .84f)
            aiX += (targetX - aiX) * .045f
            aiY += (targetY - aiY) * .045f
            if (hypot(aiX - ballX, aiY - ballY) < h * .07f && Random.nextFloat() < .035f) {
                val dx = w * .04f - ballX
                val dy = h * .505f - ballY
                val d = max(1f, hypot(dx, dy))
                ballVx = dx / d * w * .025f
                ballVy = dy / d * h * .025f
            }
            lastAi = now
        }
        drawPlayer(c, aiX, aiY, Color.rgb(255, 90, 85), now)
        drawPlayer(c, playerX, playerY, Color.rgb(255, 220, 50), now)
        // Ball motion, friction and bounce
        ballX += ballVx; ballY += ballVy
        ballVx *= .988f; ballVy *= .988f
        if (ballY < h * .12f || ballY > h * .89f) ballVy *= -.78f
        if (ballX > w * .955f && ballY in (h * .42f)..(h * .59f)) {
            scoreHome++; resetBall()
        } else if (ballX < w * .045f && ballY in (h * .42f)..(h * .59f)) {
            scoreAway++; resetBall()
        }
        ballX = ballX.coerceIn(w * .045f, w * .955f); ballY = ballY.coerceIn(h * .11f, h * .90f)
        p.color = Color.argb(75, 0, 0, 0); c.drawOval(RectF(ballX - h*.025f, ballY + h*.012f, ballX + h*.025f, ballY + h*.03f), p)
        p.color = Color.WHITE; c.drawCircle(ballX, ballY, h * .018f, p)
        // Scoreboard and player HUD
        panel(c, w * .25f, 0f, w * .75f, h * .075f, Color.WHITE)
        text(c, teams[teamIndex] + "  " + scoreHome + " - " + scoreAway + "  " + teams[opponentIndex], w / 2, h * .052f, h * .042f, Color.BLACK)
        text(c, "00:" + seconds.toString().padStart(2, '0'), w * .91f, h * .055f, h * .035f, Color.WHITE)
        val captain = playerNames.firstOrNull() ?: "Captain"
        text(c, "CAPTAIN: " + captain, w * .17f, h * .055f, h * .028f, Color.WHITE)
        panel(c, w * .035f, h * .74f, w * .20f, h * .91f, Color.argb(185, 20, 30, 42))
        text(c, "MOVE", w * .117f, h * .83f, h * .04f, Color.WHITE)
        button(c, "PASS", w * .70f, h * .76f, w * .82f, h * .90f, Color.rgb(34, 112, 170))
        button(c, "SHOOT", w * .84f, h * .76f, w * .97f, h * .90f, Color.rgb(20, 160, 105))
        text(c, formations[formationIndex] + "  •  " + teams[teamIndex], w * .5f, h * .975f, h * .027f, Color.WHITE)
    }

    private fun resetBall() {
        ballX = lastW * .5f; ballY = lastH * .505f; ballVx = 0f; ballVy = 0f
        playerX = lastW * .30f; playerY = lastH * .50f
        aiX = lastW * .70f; aiY = lastH * .50f
        lastGoal = System.currentTimeMillis()
    }

    private fun drawPlayer(c: Canvas, x: Float, y: Float, color: Int, time: Long) {
        val bob = kotlin.math.sin(time / 115.0).toFloat() * lastH * .003f
        p.color = Color.argb(70, 0, 0, 0); c.drawOval(RectF(x-lastH*.025f, y+lastH*.025f, x+lastH*.025f, y+lastH*.04f), p)
        p.color = color; c.drawCircle(x, y + bob, lastH * .024f, p)
        p.color = Color.rgb(25, 30, 35); c.drawCircle(x, y - lastH * .032f + bob, lastH * .014f, p)
    }

    private fun drawResult(c: Canvas, w: Float, h: Float) {
        c.drawColor(Color.rgb(7, 13, 24))
        text(c, "FULL TIME", w / 2, h * .20f, h * .12f, Color.WHITE)
        text(c, teams[teamIndex] + "  " + scoreHome + " - " + scoreAway + "  " + teams[opponentIndex], w / 2, h * .38f, h * .065f, Color.rgb(70, 220, 160))
        val won = scoreHome > scoreAway
        text(c, if (scoreHome == scoreAway) "MATCH DRAWN" else if (won) "VICTORY!" else "BETTER LUCK NEXT TIME", w / 2, h * .52f, h * .07f, Color.WHITE)
        if (tournamentRound > 4 && tournamentWins > 0) text(c, "CUP CHAMPION • WINS " + tournamentWins, w / 2, h * .63f, h * .05f, Color.rgb(255, 210, 65))
        button(c, if (tournamentRound <= 4 && won) "NEXT CUP MATCH" else "PLAY AGAIN", w * .34f, h * .72f, w * .66f, h * .86f)
        button(c, "HOME", w * .07f, h * .83f, w * .25f, h * .95f, Color.rgb(60, 72, 88))
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action != MotionEvent.ACTION_DOWN && e.action != MotionEvent.ACTION_MOVE) return true
        val x = e.x; val y = e.y; val w = width.toFloat(); val h = height.toFloat()
        when (screen) {
            0 -> {
                if (y in (h*.38f)..(h*.55f)) {
                    if (x < w*.38f) teamIndex = (teamIndex - 1 + teams.size) % teams.size
                    else if (x > w*.62f) teamIndex = (teamIndex + 1) % teams.size
                    selectedPlayers = (0..10).toMutableSet()
                } else if (y in (h*.58f)..(h*.76f)) {
                    if (x < w*.40f) screen = 1 else if (x < w*.65f) screen = 2 else { tournamentRound = 1; tournamentWins = 0; matchNumber = 1; screen = 3 }
                } else if (y > h*.78f) startMatch()
            }
            1 -> {
                if (y > h*.80f) {
                    if (x < w*.28f) screen = 0
                    else if (x < w*.70f) selectedPlayers = (0..10).toMutableSet()
                    else screen = 0
                } else if (y > h*.27f) {
                    val col = ((x / w * 6.45f).toInt()).coerceIn(0, 5)
                    val row = if (y < h*.52f) 0 else 1
                    val index = row * 6 + col
                    if (index < playerNames.size) {
                        if (selectedPlayers.contains(index)) selectedPlayers.remove(index)
                        else if (selectedPlayers.size < 11) selectedPlayers.add(index)
                    }
                }
            }
            2 -> {
                if (y > h*.82f) screen = 0
                else if (y > h*.32f && y < h*.80f) formationIndex = ((y - h*.34f) / (h*.12f)).toInt().coerceIn(0, 3)
            }
            3 -> {
                if (y > h*.70f && x > w*.28f) startMatch(true)
                else if (y > h*.82f && x < w*.25f) screen = 0
            }
            4 -> {
                if (x < w*.24f && y > h*.70f) {
                    playerX = (playerX + w*.06f).coerceAtMost(w*.80f)
                    playerY = (playerY + h*.02f).coerceIn(h*.15f, h*.87f)
                } else if (x > w*.82f && y > h*.72f) {
                    val dx = w*.98f - ballX; val dy = h*.505f - ballY; val d = max(1f, hypot(dx, dy))
                    ballX = playerX + w*.035f; ballY = playerY
                    ballVx = dx / d * w*.045f; ballVy = dy / d * h*.045f
                } else if (x > w*.68f && y > h*.72f) {
                    val dx = w*.55f - ballX; val dy = playerY - ballY; val d = max(1f, hypot(dx, dy))
                    ballX = playerX + w*.025f; ballY = playerY
                    ballVx = dx / d * w*.025f; ballVy = dy / d * h*.025f
                } else {
                    playerX = x.coerceIn(w*.08f, w*.82f); playerY = y.coerceIn(h*.15f, h*.87f)
                    if (hypot(playerX-ballX, playerY-ballY) < h*.11f) { ballX = playerX + w*.03f; ballY = playerY }
                }
            }
            5 -> {
                if (y > h*.80f && x < w*.28f) screen = 0
                else if (y > h*.68f) {
                    if (tournamentRound <= 4 && scoreHome > scoreAway) { matchNumber++; startMatch(true) }
                    else startMatch()
                }
            }
        }
        invalidate()
        return true
    }
}
