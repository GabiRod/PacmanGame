package org.pondar.pacmankotlin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //reference to the game class.
    private var game: Game? = null
    private var myTimer: Timer = Timer()
    private var gameTime: Timer = Timer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //makes sure it always runs in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        game = Game(this,pointsView, timeView)

        //We will call the timer 5 times each second
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }

        }, 0, 100)

        gameTime.schedule(object : TimerTask() {
            override fun run() {
                gameTimeMethod()
            }

        }, 0, 1000)

        //intialize the game view clas and game class
        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()

        resetButton.setOnClickListener {
            game?.restartGame()
        }
        pauseButton.setOnClickListener {
            game?.pauseGame()
        }
        continueButton.setOnClickListener {
            game?.continueGame()
        }
        moveRight.setOnClickListener {
            game?.movePacmanRight(10)
        }
        moveLeft.setOnClickListener {
            game?.movePacmanLeft(10)
        }
        moveTop.setOnClickListener {
            game?.movePacmanTop(10)
        }
        moveBottom.setOnClickListener {
            game?.movePacmanBottom(10)
        }
    }

    override fun onStop() {
        super.onStop()
        //just to make sure if the app is killed, that we stop the timer.
        myTimer.cancel()
    }

    private fun gameTimeMethod() {
        this.runOnUiThread(countDown)
    }

    private val countDown = Runnable {
        if (game?.running!!) {
            game!!.countDown()
            Log.d("time", game!!.timeLeft.toString())
            if ( game!!.timeLeft <= 0){
                game!!.gameOver()
            }
        }
    }

    private fun timerMethod() {
         this.runOnUiThread(timerTick)
    }

    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (game?.running!!) {
            game!!.counter++
            if (game?.enemyDirection== 1) {
                game!!.moveEnemiesRight(10)
            }
            else if(game?.enemyDirection== 2){

                game!!.moveEnemiesLeft(10)
            }
            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second
            // game.text = getString(R.string.timerValue,counter)

            if (game?.direction==1)
            { // move right
                game!!.movePacmanRight(10)
                //move the pacman - you
                //should call a method on your game class to move
                //the pacman instead of this - you have already made that
            }
            else if (game?.direction==2)
            {
                game!!.movePacmanLeft(10)
            }
            else if (game?.direction==3)
            {
                game!!.movePacmanTop(10)
            }
            else if (game?.direction==4)
            {
                game!!.movePacmanBottom(10)
            }



        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_LONG).show()
            game?.newGame()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
