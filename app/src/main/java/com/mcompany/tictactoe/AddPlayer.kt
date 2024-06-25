package com.mcompany.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class AddPlayer : AppCompatActivity() {

    private var isChampionshipMode = false
    private var isTimerMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_add_player)

        val playerNamesCardView = findViewById<CardView>(R.id.playerNamesCardView)
        val modeCardView = findViewById<CardView>(R.id.modeCardView)
        val playerOne = findViewById<EditText>(R.id.playerOne)
        val playerTwo = findViewById<EditText>(R.id.playerTwo)
        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val multiplePlayerButton = findViewById<Button>(R.id.multiplePlayerButton)
        val singlePlayerButton = findViewById<Button>(R.id.singlePlayerButton)
        val promptTextView = findViewById<TextView>(R.id.promptTextView)
        val championshipButton = findViewById<Button>(R.id.championshipButton)
        val timerButton = findViewById<Button>(R.id.timerButton)
        val backButton = findViewById<ImageView>(R.id.imageViewBack)

        multiplePlayerButton.setOnClickListener {
            modeCardView.visibility = View.INVISIBLE
            playerNamesCardView.visibility = View.VISIBLE

            backButton.setOnClickListener{
                modeCardView.visibility = View.VISIBLE
                playerNamesCardView.visibility = View.GONE
            }


        }

        singlePlayerButton.setOnClickListener {
            modeCardView.visibility = View.INVISIBLE
            playerNamesCardView.visibility = View.VISIBLE

            backButton.setOnClickListener{
                modeCardView.visibility = View.VISIBLE
                playerNamesCardView.visibility = View.GONE
            }
            promptTextView.text = "Enter your name"
            playerOne.hint = "Enter your name"

            playerTwo.setText("TicTacAI")
            playerTwo.isEnabled = false
            playerTwo.isFocusable = false
            playerTwo.clearFocus()
        }

        championshipButton.setOnClickListener {
            isChampionshipMode = !isChampionshipMode
            updateButtonState(championshipButton, isChampionshipMode)
        }

        timerButton.setOnClickListener {
            isTimerMode = !isTimerMode
            updateButtonState(timerButton, isTimerMode)
        }




        startGameButton.setOnClickListener {
            val getPlayerOneName = playerOne.text.toString()
            val getPlayerTwoName = playerTwo.text.toString()

            if (getPlayerOneName.isEmpty() || getPlayerTwoName.isEmpty()) {
                Toast.makeText(this@AddPlayer, "Please enter player name", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@AddPlayer, MainActivity::class.java).apply {
                    putExtra("playerOne", getPlayerOneName)
                    putExtra("playerTwo", getPlayerTwoName)
                    putExtra("isChampionshipMode", isChampionshipMode)
                    putExtra("isTimerMode", isTimerMode)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateButtonState(button: Button, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }
}