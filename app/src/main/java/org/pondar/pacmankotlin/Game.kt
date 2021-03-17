package org.pondar.pacmankotlin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Array.set
import java.util.*
import kotlin.random.Random


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView, time: TextView) {

    var pointsView: TextView = view
    var gameTime: TextView = time
    var running = false
    var direction = 1
    var enemyDirection = 1
    var counter: Int = 0
    var timeLeft: Int = 60
    var points: Int = 0
    var coinNumber: Int = 10
    var level: Int =1

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var coinBitmap: Bitmap
    var enemyBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0


    //enemies and coins initialized
    var coinsInitialized = false
    var enemiesInitialized = false

    //the list of goldcoins and emenies - initially empty
    var coins = ArrayList<GoldCoin>()
    var enemies = ArrayList<Enemy>()

    //a reference to the gameview
    private var gameView: GameView? = null
    var h: Int = 0
    var w: Int = 0 //height and width of screen

    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        enemyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemy)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun initializeGoldcoins() {

        for (c in 1..10) {
            var c = GoldCoin(false, (0..1000).random(), (0..1400).random())
            coins.add(c)

        }
        coinsInitialized = true
    }


    fun initializeEnemies() {

        if (level == 1) {
            for (e in 1..1) {
                val e = Enemy(true, (0..900).random(), (200..1400).random())
                enemies.add(e)
            }
        }

        else if (level == 2) {

            for (e in 1..2) {
                val e = Enemy(true, (0..900).random(), (200..1400).random())
                enemies.add(e)

            }
        }

        else if (level == 3) {
            for (e in 1..3) {
                val e = Enemy(true, (0..900).random(), (200..1400).random())
                enemies.add(e)
            }
        }
        enemiesInitialized = true
    }

    fun newGame() {
        pacx = 5
        pacy = 10 //just some starting coordinates - you can change this.
        coins = ArrayList<GoldCoin>()
        coinsInitialized = false
        initializeGoldcoins()
        enemies = ArrayList<Enemy>()
        enemiesInitialized = false
        initializeEnemies()
        running = true //should the game be running?
        points = 0
        level = level
        timeLeft = 60
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        gameTime.text = "${context.resources.getString(R.string.gameTime)} $timeLeft"
        gameView?.invalidate() //redraw screen
        direction = 1
        enemyDirection = 1

    }

     fun countDown(){
         timeLeft--
         gameTime.text = "${context.resources.getString(R.string.gameTime)} $timeLeft"

     }

    fun movePacmanRight(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels + pacBitmap.width < w) {
            pacx += pixels
            doCollisionCheck()
            gameView!!.invalidate()
            direction = 1
        }
    }

    fun movePacmanLeft(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels > 0) {
            pacx -= pixels
            doCollisionCheck()
            gameView!!.invalidate()
            direction = 2
        }
    }

    fun movePacmanTop(pixels: Int) {
        //still within our boundaries?
        if (pacy + pixels > 10) {
            pacy -= pixels
            doCollisionCheck()
            gameView!!.invalidate()
            direction = 3
        }
    }

    fun movePacmanBottom(pixels: Int) {
        //still within our boundaries?
        if (pacy + pixels + pacBitmap.height < h) {
            pacy += pixels
            doCollisionCheck()
            gameView!!.invalidate()
            direction = 4
        }
    }

    fun moveEnemiesRight(pixels: Int) {
        for (enemy in enemies)
        {
           if (enemy.enemyx + pixels + pacBitmap.width > w)
            {
                Log.d("enemy", "moveEnemiesRight")
                enemyDirection= 2
            }else {
               enemy.enemyx += pixels
               gameView!!.invalidate()}
        }
    }
    fun moveEnemiesLeft(pixels: Int) {
        for (enemy in enemies)
        {
            if (enemy.enemyx + pixels < 0)
            {
                Log.d("enemy", "moveEnemiesLeft")
                enemyDirection= 1
            }else {
                enemy.enemyx -= pixels
                gameView!!.invalidate()
            }

        }
    }


    fun pauseGame() {
        running = false
    }

    fun gameOver() {
        running = false
        Toast.makeText(context, "GAME OVER", Toast.LENGTH_LONG).show()

    }

    fun continueGame() {
        running = true
    }

    //TODO check if the pacman touches a gold coin
    fun distance(x2: Int, x1: Int, y2: Int, y1: Int): Double {
        val distance = Math.sqrt((((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toDouble()))
        Log.d("distance", distance.toString())
        return distance;
    }


    fun doCollisionCheck() {

        for (coin in coins) {
            val distance = distance(coin.coinx, pacx, coin.coiny, pacy)

            if (distance <= 150 && !coin.taken) {
                coin.taken = true
                val mediaPlayer = MediaPlayer.create(context, R.raw.coinup)
                mediaPlayer.start()
                points++
                pointsView.text = "${context.resources.getString(R.string.points)} $points"
                coinNumber--
                gameView?.invalidate()

                Log.d("points", points.toString())
            }
        }

        for (e in enemies) {
            val distance = distance(e.enemyx, pacx, e.enemyy, pacy)

            if (distance <= 150 && e.alive) {
                e.alive = false
                coinNumber = 10
                gameOver()
                Log.d("enemy", e.alive.toString())
                gameView?.invalidate()
                val mediaPlayer = MediaPlayer.create(context, R.raw.dead)
                mediaPlayer.start()

            }

        }
        if (coinNumber == 0 && timeLeft >=1){
            coinNumber = 10
            running= false
            levelUp()
        }

    }
    
    fun levelUp(){
        if (level <= 3) {
            level++
            newGame()
            running= true
            Toast.makeText(context, "LEVEL UP", Toast.LENGTH_SHORT).show()
            Log.d("win", level.toString())
        }else{
            Toast.makeText(context, "YOU WON THIS GAME!", Toast.LENGTH_SHORT).show()
            Log.d("win", "win")
        }
    }

}