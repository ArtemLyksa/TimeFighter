package com.example.timefighter.lyksa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    internal var score = 0
    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    internal lateinit var countDownTimer: CountDownTimer
    internal var gameStarted: Boolean = false
    internal val initialCountDown: Long = 50000
    internal val countDownInterval: Long = 1000
    internal var timeLeft: Long = 50000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate is called. Score is: $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)
        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if (savedInstanceState != null) {
            timeLeft = savedInstanceState.getLong(TIME_LEFT_KEY)
            score = savedInstanceState.getInt(SCORE_KEY)
            setupTimer()
            if (timeLeft < initialCountDown) {
                countDownTimer.start()
            }
        } else {
            setupTimer(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState Score: $score, timeLeft: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun setupTimer(reset: Boolean = false) {
        if (reset) {
            timeLeft = initialCountDown
            score = 0
        }
        gameScoreTextView.text = getString(R.string.yourScore, score)

        val countTimeLeft = timeLeft / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, countTimeLeft)

        countDownTimer = object : CountDownTimer(timeLeft, countDownInterval) {
            override fun onTick(p0: Long) {
                timeLeft = p0
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft / 1000)
            }

            override fun onFinish() {
                endGame()
            }

        }
        gameStarted = false
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score += 1
        val scoreText = getString(R.string.yourScore, score)
        gameScoreTextView.text = scoreText
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        gameScoreTextView.startAnimation(animation)
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        setupTimer(true)
    }
}