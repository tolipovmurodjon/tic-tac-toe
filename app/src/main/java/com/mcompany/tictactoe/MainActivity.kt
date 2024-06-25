package com.mcompany.tictactoe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mcompany.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val combinationList: MutableList<IntArray> = mutableListOf(
        intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
        intArrayOf(2, 4, 6), intArrayOf(0, 4, 8)
    )
    private var boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var playerTurn = 1
    private var totalSelectedBoxes = 1
    private var mediaPlayer: MediaPlayer? = null
    private var getPlayerOneName: String? = null
    private var getPlayerTwoName: String? = null
    private var isChampionshipMode = false
    private var totalGames = 10
    private var currentGame = 1
    private var playerOneWins = 0
    private var playerTwoWins = 0
    private var isTimerMode = false
    private var playerOneTimer: CountDownTimerExt? = null
    private var playerTwoTimer: CountDownTimerExt? = null
    private var timerDuration: Long = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentExtras()
        setupUI()
        setupClickListeners()
        highlightPlayerTurn()
        firstMove()
    }

    private fun getIntentExtras(): Boolean {
        getPlayerOneName = intent.getStringExtra("playerOne")
        getPlayerTwoName = intent.getStringExtra("playerTwo")
        isChampionshipMode = intent.getBooleanExtra("isChampionshipMode", false)
        isTimerMode = intent.getBooleanExtra("isTimerMode", false)
        return true
    }

    private fun setupUI() {
        binding?.playerOneName?.text = getPlayerOneName
        binding?.playerTwoName?.text = getPlayerTwoName
        binding?.allGamesTextView?.text = "$currentGame/$totalGames"
        binding?.playerOneResultTextView?.text = "$playerOneWins"
        binding?.playerTwoResultTextView?.text = "$playerTwoWins"

        if (!isChampionshipMode){
            binding?.customToolbar?.visibility = View.GONE
            binding?.playerOneResultTextView?.visibility = View.GONE
            binding?.playerTwoResultTextView?.visibility = View.GONE
        }

        if (isTimerMode) {
            binding?.playerOneTimerTextView?.visibility = View.VISIBLE
            binding?.playerTwoTimerTextView?.visibility = View.VISIBLE

            if (isChampionshipMode){
                timerDuration = 30000
                binding?.playerOneTimerTextView?.text = "30:00"
                binding?.playerTwoTimerTextView?.text = "30:00"

                binding?.optionsTitleTextView?.text = "🏆 Championship & ⌛ Timer"

            } else{
                binding?.optionsTitleTextView?.text = "⌛ Timer"
            }

            setupTimers()
        } else{
            binding?.optionsTitleTextView?.text = "🏆 Championship"
        }
    }

    private fun setupTimers() {
        playerOneTimer = object : CountDownTimerExt(timerDuration, 10) {
            override fun onTimerTick(millisUntilFinished: Long) {
                updateTimerTextView(binding?.playerOneTimerTextView, millisUntilFinished)
            }

            override fun onTimerFinish() {
                playerLose(getPlayerOneName)
            }

        }

        playerTwoTimer = object : CountDownTimerExt(timerDuration, 10) {
            override fun onTimerTick(millisUntilFinished: Long) {
                updateTimerTextView(binding?.playerTwoTimerTextView, millisUntilFinished)
            }

            override fun onTimerFinish() {
                playerLose(getPlayerTwoName)
            }

        }

    }

    private fun updateTimerTextView(timerTextView: TextView?, millisUntilFinished: Long) {
        val seconds = millisUntilFinished / 1000
        val milliseconds = millisUntilFinished % 1000 / 10
        timerTextView?.text = String.format("%02d:%02d", seconds, milliseconds)
    }

    private fun playerLose(playerName: String?) {
        customDialog("😔 $playerName ran out of time and lost the game!")
    }

    private fun firstMove(){
        playerTurn = (1..2).random()

        if (getPlayerTwoName == "TicTacAI" && playerTurn == 2) {
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.white_box)
            simulateAIMove()
        } else if(getPlayerTwoName == "TicTacAI" && playerTurn == 1){
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.white_box)
        } else if(playerTurn == 2){
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.white_box)
        }

        if (isTimerMode) {
            if (playerTurn == 1) {
                playerOneTimer?.start()
            } else {
                playerTwoTimer?.start()

                if(getPlayerTwoName == "TicTacAI"){
                    simulateAIMove()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding?.image1?.setOnClickListener { view ->
            if (isBoxSelectable(0)) {
                performAction(view as ImageView, 0)
            }
        }
        binding?.image2?.setOnClickListener { view ->
            if (isBoxSelectable(1)) {
                performAction(view as ImageView, 1)
            }
        }
        binding?.image3?.setOnClickListener { view ->
            if (isBoxSelectable(2)) {
                performAction(view as ImageView, 2)
            }
        }
        binding?.image4?.setOnClickListener { view ->
            if (isBoxSelectable(3)) {
                performAction(view as ImageView, 3)
            }
        }
        binding?.image5?.setOnClickListener { view ->
            if (isBoxSelectable(4)) {
                performAction(view as ImageView, 4)
            }
        }
        binding?.image6?.setOnClickListener { view ->
            if (isBoxSelectable(5)) {
                performAction(view as ImageView, 5)
            }
        }
        binding?.image7?.setOnClickListener { view ->
            if (isBoxSelectable(6)) {
                performAction(view as ImageView, 6)
            }
        }
        binding?.image8?.setOnClickListener { view ->
            if (isBoxSelectable(7)) {
                performAction(view as ImageView, 7)
            }
        }
        binding?.image9?.setOnClickListener { view ->
            if (isBoxSelectable(8)) {
                performAction(view as ImageView, 8)
            }
        }
    }

    private fun performAction(imageView: ImageView, selectedBoxPosition: Int) {
        boxPositions[selectedBoxPosition] = playerTurn
        if (playerTurn == 1) {
            imageView.setImageResource(R.drawable.ximage)
            if (checkResults()) {


                if (isChampionshipMode){
                    playerOneWins++
                    restartMatch()


                }  else{

                    if(isTimerMode){
                        playerTwoTimer?.restart()
                        playerOneTimer?.restart()
                    }
                    customDialog(binding?.playerOneName?.text.toString() + " is a Winner!")
                }



            } else if (totalSelectedBoxes == 9) {

                if (isChampionshipMode){
                    restartMatch()

                } else{
                    if(isTimerMode){
                        playerTwoTimer?.restart()
                        playerOneTimer?.restart()
                    }
                    customDialog("Match Draw")
                }


            } else {
                changePlayerTurn(2)
                totalSelectedBoxes++

                if (getPlayerTwoName == "TicTacAI") {
                    simulateAIMove()
                }
            }
        } else {
            imageView.setImageResource(R.drawable.oimage)
            if (checkResults()) {
                if (isChampionshipMode){
                    playerTwoWins++
                    restartMatch()

                } else{
                    if(isTimerMode){
                        playerTwoTimer?.restart()
                        playerOneTimer?.restart()
                    }
                    customDialog(binding?.playerTwoName?.text.toString() + " is a Winner!")
                }
            } else if (totalSelectedBoxes == 9) {
                if (isChampionshipMode){
                    restartMatch()

                } else{
                    if(isTimerMode){
                        playerTwoTimer?.restart()
                        playerOneTimer?.restart()
                    }
                    customDialog("Match Draw")
                }
            } else {
                changePlayerTurn(1)
                totalSelectedBoxes++


            }
        }
    }

    private fun changePlayerTurn(currentPlayerTurn: Int) {
        playerTurn = currentPlayerTurn
        if (playerTurn == 1) {
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.white_box)

            playerTwoTimer?.pause()
            playerOneTimer?.start()

        } else {
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.white_box)

            playerTwoTimer?.start()
            playerOneTimer?.pause()


        }
    }

    private fun checkResults(): Boolean {
        var response = false
        for (i in combinationList.indices) {
            val combination = combinationList[i]
            if ((boxPositions[combination[0]] == playerTurn) &&
                (boxPositions[combination[1]] == playerTurn) &&
                (boxPositions[combination[2]] == playerTurn)
            ) {
                response = true
            }
        }
        return response
    }

    private fun isBoxSelectable(boxPosition: Int): Boolean {
        return boxPositions[boxPosition] == 0
    }

    private fun restartMatch() {
        boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0) // 9 zeros
        totalSelectedBoxes = 1
        binding?.image1?.setImageResource(R.drawable.white_box)
        binding?.image2?.setImageResource(R.drawable.white_box)
        binding?.image3?.setImageResource(R.drawable.white_box)
        binding?.image4?.setImageResource(R.drawable.white_box)
        binding?.image5?.setImageResource(R.drawable.white_box)
        binding?.image6?.setImageResource(R.drawable.white_box)
        binding?.image7?.setImageResource(R.drawable.white_box)
        binding?.image8?.setImageResource(R.drawable.white_box)
        binding?.image9?.setImageResource(R.drawable.white_box)






        if (isChampionshipMode && currentGame<totalGames){
            currentGame++
            binding?.allGamesTextView?.text = "$currentGame/$totalGames"
            binding?.playerOneResultTextView?.text = "$playerOneWins"
            binding?.playerTwoResultTextView?.text = "$playerTwoWins"
        } else if (isChampionshipMode && currentGame == totalGames ){

            binding?.playerOneResultTextView?.text = "$playerOneWins"
            binding?.playerTwoResultTextView?.text = "$playerTwoWins"

            stopTimer()

            when{
                playerOneWins>playerTwoWins -> {
                    customDialog(binding?.playerOneName?.text.toString() + " is a Winner!")
                }
                playerOneWins<playerTwoWins -> {
                    customDialog(binding?.playerTwoName?.text.toString() + " is a Winner!")
                }
                playerOneWins == playerTwoWins -> {
                    customDialog("Draw Match")
                }

            }

        }


        firstMove()






    }

    private fun rematchChampionship(){
        boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0) // 9 zeros
        totalSelectedBoxes = 1
        playerOneWins = 0
        playerTwoWins = 0
        currentGame = 1

        binding?.allGamesTextView?.text = "$currentGame/$totalGames"
        binding?.playerOneResultTextView?.text = "$playerOneWins"
        binding?.playerTwoResultTextView?.text = "$playerTwoWins"

        binding?.image1?.setImageResource(R.drawable.white_box)
        binding?.image2?.setImageResource(R.drawable.white_box)
        binding?.image3?.setImageResource(R.drawable.white_box)
        binding?.image4?.setImageResource(R.drawable.white_box)
        binding?.image5?.setImageResource(R.drawable.white_box)
        binding?.image6?.setImageResource(R.drawable.white_box)
        binding?.image7?.setImageResource(R.drawable.white_box)
        binding?.image8?.setImageResource(R.drawable.white_box)
        binding?.image9?.setImageResource(R.drawable.white_box)

        if (isTimerMode){
            setupTimers()
            firstMove()

            if (isChampionshipMode){
                binding?.playerOneTimerTextView?.text = "30:00"
                binding?.playerTwoTimerTextView?.text = "30:00"
            } else{
                binding?.playerOneTimerTextView?.text = "10:00"
                binding?.playerTwoTimerTextView?.text = "10:00"
            }

        }

    }

    private fun customDialog(result : String){

        val customDialog = Dialog(this, R.style.CustomDialogTheme)

        customDialog.setContentView(R.layout.dialog_result)

        customDialog.findViewById<TextView>(R.id.messageText).text = result

        customDialog.findViewById<Button>(R.id.rematchButton).setOnClickListener {

            if(isChampionshipMode || isTimerMode){
                rematchChampionship()
                customDialog.dismiss()
            } else{
                restartMatch()
                customDialog.dismiss()
                mediaPlayer?.release()
                mediaPlayer = null
            }

        }

        customDialog.findViewById<Button>(R.id.newGameButton).setOnClickListener{
            val intent = Intent(this@MainActivity, AddPlayer::class.java)
            startActivity(intent)
        }

        val confettiAnimation = customDialog.findViewById<ImageView>(R.id.confettiAnimation)

        val cupImageView =  customDialog.findViewById<ImageView>(R.id.cupImageView)

        if(isChampionshipMode){

            val zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom)

            cupImageView.startAnimation(zoomAnimation)

            cupImageView.visibility = View.VISIBLE



        }



        // Load and display the GIF
        Glide.with(this)
            .asGif()
            .load(R.raw.confetti_main) // Your GIF file in raw folder
            .into(confettiAnimation)
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        confettiAnimation.visibility = View.VISIBLE
        confettiAnimation.startAnimation(slideDown)

        playCongratsSound()

        customDialog.setCancelable(false)
        customDialog.show()
    }

    private fun exitDialog(){

        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_exit)
        customDialog.findViewById<Button>(R.id.buttonExit).setOnClickListener{
            super.onBackPressedDispatcher.onBackPressed()
            customDialog.dismiss()


        }

        customDialog.findViewById<TextView>(R.id.buttonCancel).setOnClickListener{
            customDialog.dismiss()

        }
        customDialog.show()
    }

    private fun playCongratsSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    private fun simulateAIMove() {

        // Check if there's a move to win
        var aiMove = findWinningMove()

        if (aiMove == -1) {
            // Check if there's a move to block player from winning
            aiMove = findBlockingMove()
        }

        if (aiMove == -1) {
            // If no winning or blocking moves, choose a random move
            val emptyBoxes = mutableListOf<Int>()
            for (i in boxPositions.indices) {
                if (boxPositions[i] == 0) {
                    emptyBoxes.add(i)
                }
            }
            aiMove = emptyBoxes.random()
        }

        // Perform the chosen move
        performAction(findViewById(getImageViewId(aiMove)), aiMove)
    }

    private fun findWinningMove(): Int {
        // Iterate through each winning combination
        for (combination in combinationList) {
            // Check if AI can complete this combination to win
            if (boxPositions[combination[0]] == playerTurn &&
                boxPositions[combination[1]] == playerTurn &&
                boxPositions[combination[2]] == 0) {
                return combination[2]
            } else if (boxPositions[combination[0]] == playerTurn &&
                boxPositions[combination[1]] == 0 &&
                boxPositions[combination[2]] == playerTurn) {
                return combination[1]
            } else if (boxPositions[combination[0]] == 0 &&
                boxPositions[combination[1]] == playerTurn &&
                boxPositions[combination[2]] == playerTurn) {
                return combination[0]
            }
        }
        return -1 // No winning move found
    }

    private fun findBlockingMove(): Int {
        // Determine opponent's turn
        val opponentTurn = if (playerTurn == 1) 2 else 1

        // Iterate through each winning combination
        for (combination in combinationList) {
            // Check if opponent can complete this combination to win
            if (boxPositions[combination[0]] == opponentTurn &&
                boxPositions[combination[1]] == opponentTurn &&
                boxPositions[combination[2]] == 0) {
                return combination[2]
            } else if (boxPositions[combination[0]] == opponentTurn &&
                boxPositions[combination[1]] == 0 &&
                boxPositions[combination[2]] == opponentTurn) {
                return combination[1]
            } else if (boxPositions[combination[0]] == 0 &&
                boxPositions[combination[1]] == opponentTurn &&
                boxPositions[combination[2]] == opponentTurn) {
                return combination[0]
            }
        }
        return -1 // No blocking move found
    }

    private fun getImageViewId(position: Int): Int {
        return when (position) {
            0 -> R.id.image1
            1 -> R.id.image2
            2 -> R.id.image3
            3 -> R.id.image4
            4 -> R.id.image5
            5 -> R.id.image6
            6 -> R.id.image7
            7 -> R.id.image8
            8 -> R.id.image9
            else -> R.id.image1
        }
    }

    private fun highlightPlayerTurn() {
        if (playerTurn == 1) {
            // Highlight Player 1 (X)
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.white_box)
        } else {
            // Highlight Player 2 (O)
            binding?.playerTwoTitleLayout?.setBackgroundResource(R.drawable.black_border)
            binding?.playerOneTitleLayout?.setBackgroundResource(R.drawable.white_box)
        }
    }

    private fun stopTimer() {
        playerOneTimer?.cancel()
        playerTwoTimer?.cancel()
        playerOneTimer = null
        playerTwoTimer = null
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        exitDialog()

    }
}