package com.example.sleeptimer

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var circularSeekBar: CircularSeekBar
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)
        circularSeekBar = findViewById(R.id.circularSeekBar)

        circularSeekBar.setOnCircularSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar, progress: Float, fromUser: Boolean) {
                updateTimerText(progress.toInt())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
            }
        })
    }

    private fun updateTimerText(progress: Int) {
        timerTextView.text = String.format("%02d:%02d", progress / 60, progress % 60)
    }

    private fun startTimer(durationInSeconds: Int) {
        countDownTimer = object : CountDownTimer((durationInSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
            }
        }

        countDownTimer.start()
    }
}
