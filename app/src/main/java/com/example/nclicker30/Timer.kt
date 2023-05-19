package com.example.nclicker30

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nclicker30.SQLiteHelper.Companion.TABLE_NAME3
import java.util.*
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView


class Timer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var countdownTimer: CountDownTimer
    private var timeRemaining: Long = 0
    private var isTimerRunning: Boolean = false
    private var puntos: Int = 0
    private var vidas: Int = 3
    private lateinit var rachaText: TextView
    private lateinit var textoracha: TextView
    private lateinit var tvRachaRecord: TextView
    private lateinit var countdownText: TextView
    private lateinit var startButton: Button
    private lateinit var botonMenu: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var imageViewCorazones: ImageView

    private lateinit var dbHelper: SQLiteHelper
    private lateinit var db: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        drawerLayout = findViewById(R.id.drawer_layoutTimer)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Obtener una referencia al ProgressBar y asignar el drawable creado

        val animacion = AnimationUtils.loadAnimation(this, R.anim.presionar_boton)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        countdownText = findViewById(R.id.tvTiempo)
        startButton = findViewById(R.id.btParar)
        rachaText = findViewById(R.id.tvracha)
        tvRachaRecord = findViewById(R.id.tvRachaRecord)
        botonMenu = findViewById(R.id.btmenu)
        imageViewCorazones = findViewById(R.id.ivVidas)
        imageViewCorazones.setImageResource(R.drawable.trescorazones)

        dbHelper = SQLiteHelper(this)
        db = dbHelper.writableDatabase

        // Crear una animación de escala
        val animacion3 = ValueAnimator.ofFloat(1.0f, 0.95f, 1.0f)
        animacion3.duration = 1500 // Duración de la animación en milisegundos
        animacion3.repeatCount = ValueAnimator.INFINITE // Repetir la animación indefinidamente
        animacion3.repeatMode = ValueAnimator.REVERSE // Invertir la animación después de cada ciclo

        // Actualizar la escala del botón en cada fotograma de la animación
        animacion3.addUpdateListener { valueAnimator ->
            val factorEscala = valueAnimator.animatedValue as Float
            startButton.scaleX = factorEscala
            startButton.scaleY = factorEscala
            imageViewCorazones.scaleX = factorEscala
            imageViewCorazones.scaleY = factorEscala
        }
        // Iniciar la animación
        animacion3.start()

        botonMenu.setOnClickListener { view ->
            botonMenu.startAnimation(animacion)
            abrirMenu(view)
        }
        val navView: NavigationView = findViewById(R.id.nav_viewTimer)
        navView.setNavigationItemSelectedListener(this)


        startButton.setOnClickListener {
            startButton.startAnimation(animacion)
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            if (isTimerRunning) {
                stopTimer()
            } else {
                countdownText.setTextColor(Color.BLACK)
                startTimer()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fasttap, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iresetear -> {
                // Crear un cuadro de diálogo para preguntar al usuario si realmente desea restablecer el récord
                AlertDialog.Builder(this, R.style.EstiloDialogo)
                    .setTitle("¿Estás seguro?")
                    .setMessage("¿Realmente deseas restablecer el récord?")
                    .setPositiveButton("Sí") { _, _ ->
                        // Si el usuario hace clic en "Sí", borrar el registro de la tabla NClicker2
                        val db = dbHelper.writableDatabase
                        db.delete(SQLiteHelper.TABLE_NAME3, null, null)
                        val toast = Toast.makeText(this, "El Récord se ha restablecido", Toast.LENGTH_SHORT)
                        toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                        toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                            setTextColor(ContextCompat.getColor(context, R.color.white))
                            textSize = 16f
                        }
                        toast.show()
                        tvRachaRecord.text = "0"
                    }
                    .setNegativeButton("No", null)
                    .show()
                true

            }
            R.id.icerrar -> {
                // Acción al seleccionar la opción 3
                finish()
                true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun abrirMenu(view: View) {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layoutTimer)
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(10000, 9) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                timeRemaining = 0
                isTimerRunning = false
                countdownText.setTextColor(Color.RED)
                vidas--
                when (vidas) { // Verificar la cantidad de vidas restantes y actualizar la imagen correspondiente
                    2 -> imageViewCorazones.setImageResource(R.drawable.doscorazones)
                    1 -> imageViewCorazones.setImageResource(R.drawable.uncorazon)
                    0 -> imageViewCorazones.setImageResource(R.drawable.trescorazones)
                }
                imageViewCorazones.startAnimation(AnimationUtils.loadAnimation(this@Timer, R.anim.anim_corazones))
                if (vidas == 0) {
                    AlertDialog.Builder(this@Timer, R.style.EstiloDialogo)
                        .setTitle("¡GAME OVER!")
                        .setMessage("RACHA: $puntos")
                        .setPositiveButton("Cerrar") { _, _ ->}
                        .setCancelable(false)
                    vidas = 3
                    puntos=0
                    rachaText.text = puntos.toString()
                    countdownText.setTextColor(Color.BLACK)
                } else {
                    AlertDialog.Builder(this@Timer)
                        .setTitle("¡FALLASTE!")
                        .setMessage("VIDAS RESTANTES: $vidas")
                        .setPositiveButton("Cerrar") { _, _ -> }
                        .setCancelable(false)
                    checkRecord()
                }
            }
        }
        countdownTimer.start()
        isTimerRunning = true
        startButton.text = "Stop"
    }

    private fun stopTimer() {
        val animacionfuegos = findViewById<LottieAnimationView>(R.id.animacion_fuegos)
        val animacionconfeti = findViewById<LottieAnimationView>(R.id.animacion_confeti)
        countdownTimer.cancel()
        isTimerRunning = false
        startButton.text = "Start"

        if (timeRemaining in 4950..5050) {
            animacionconfeti.setAnimation(R.raw.confeti)
            animacionconfeti.speed = 2.0f
            animacionconfeti.playAnimation()
            countdownText.setTextColor(Color.GREEN)
            puntos++
            rachaText.text = puntos.toString()

        }else if(timeRemaining in 5000 .. 5000){
            animacionfuegos.setAnimation(R.raw.fuegosartificiales)
            animacionfuegos.playAnimation()
            countdownText.setTextColor(Color.GREEN)
            puntos+2
            rachaText.text = puntos.toString()

        } else {
            countdownText.setTextColor(Color.RED)
            vidas --
            when (vidas) { // Verificar la cantidad de vidas restantes y actualizar la imagen correspondiente
                2 -> imageViewCorazones.setImageResource(R.drawable.doscorazones)
                1 -> imageViewCorazones.setImageResource(R.drawable.uncorazon)
                0 -> imageViewCorazones.setImageResource(R.drawable.trescorazones)
            }
            imageViewCorazones.startAnimation(AnimationUtils.loadAnimation(this@Timer, R.anim.anim_corazones))
            if (vidas == 0) {
                AlertDialog.Builder(this@Timer, R.style.EstiloDialogo)
                    .setTitle("¡GAME OVER!")
                    .setMessage("RACHA: $puntos")
                    .setPositiveButton("Cerrar") { _, _ ->}
                    .show()
                checkRecord()
                vidas = 3
                puntos=0
                rachaText.text = puntos.toString()
                countdownText.setTextColor(Color.BLACK)

            } else {val toast = Toast.makeText(this, "¡FALLASTE! VIDAS RESTANTES: $vidas", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
            }
        }
    }

    private fun updateCountdownText() {
        val seconds = timeRemaining / 1000
        val deciseconds = (timeRemaining % 1000) / 10
        val timeFormatted = String.format(Locale.getDefault(), "%d.%02d", seconds, deciseconds)
        countdownText.text = timeFormatted
    }

    private fun checkRecord() {
        val record = getRecord()
        if (record == null || puntos > record) {
            updateRecord()
            tvRachaRecord.text = "$puntos"
        }
    }

    private fun getRecord(): Int? {
        val cursor = db.query(
            TABLE_NAME3,
            arrayOf(COLUMNA_PUNTOS),
            null,
            null,
            null,
            null,
            "$COLUMNA_PUNTOS DESC",
            "1"
        )
        return if (cursor.moveToFirst()) cursor.getInt(0) else null
    }

    private fun updateRecord() {
        val values = ContentValues().apply {
            put(COLUMNA_PUNTOS, puntos)
        }
        if(db.update(TABLE_NAME3, values, null, null) == 0) {
            db.insert(TABLE_NAME3, null, values)
        }
    }

    companion object {
        const val COLUMNA_PUNTOS = "puntos"
    }
}


