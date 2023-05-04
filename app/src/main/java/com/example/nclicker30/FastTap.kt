package com.example.nclicker30

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Context
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.nclicker30.SQLiteHelper.Companion.TABLE_NAME2


class FastTap : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvTime: TextView
    private lateinit var btnClick: Button
    private lateinit var tvRecord: TextView
    private lateinit var botonAtras2: Button
    private lateinit var botonMenu: Button
    private lateinit var mediaPlayer: MediaPlayer

    private var puntos: Int = 0
    private var timeLeft: Long = 15000 // Tiempo en milisegundos (15 segundos)
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private lateinit var barraProgreso: ProgressBar


    private lateinit var dbHelper: SQLiteHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val animacion = AnimationUtils.loadAnimation(this, R.anim.presionar_boton)
        val animacion2 = AnimationUtils.loadAnimation(this, R.anim.anim_textview)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        tvPoints = findViewById(R.id.tvPoints)
        tvTime = findViewById(R.id.tvTime)
        btnClick = findViewById(R.id.btN)
        tvRecord = findViewById(R.id.tvRecord)
        botonAtras2 = findViewById(R.id.btatras2)
        botonMenu = findViewById(R.id.btmenu)

        dbHelper = SQLiteHelper(this)
        db = dbHelper.writableDatabase

        barraProgreso = findViewById(R.id.progressBar)
        barraProgreso.max = timeLeft.toInt() / 1000
        barraProgreso.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

        // Crear una animación de escala
        val animacion3 = ValueAnimator.ofFloat(1.0f, 0.95f, 1.0f)
        animacion3.duration = 1500 // Duración de la animación en milisegundos
        animacion3.repeatCount = ValueAnimator.INFINITE // Repetir la animación indefinidamente
        animacion3.repeatMode = ValueAnimator.REVERSE // Invertir la animación después de cada ciclo

        // Actualizar la escala del botón en cada fotograma de la animación
        animacion3.addUpdateListener { valueAnimator ->
            val factorEscala = valueAnimator.animatedValue as Float
            btnClick.scaleX = factorEscala
            btnClick.scaleY = factorEscala
            barraProgreso.scaleX = factorEscala
            barraProgreso.scaleY = factorEscala
            tvTime.scaleX = factorEscala
            tvTime.scaleY = factorEscala
        }
        // Iniciar la animación
        animacion3.start()

        botonAtras2.setOnClickListener {
            botonAtras2.startAnimation(animacion)
            onBackPressed()
        }
        botonMenu.setOnClickListener {
            botonMenu.startAnimation(animacion)
            // Crear un PopupMenu que se mostrará en el botón de menú
            val popupMenu = PopupMenu(this, botonMenu)

            // Inflar el archivo de menú en el PopupMenu
            popupMenu.menuInflater.inflate(R.menu.menu_fasttap, popupMenu.menu)

            // Configurar un OnMenuItemClickListener para el PopupMenu
            popupMenu.setOnMenuItemClickListener {
                    menuItem ->
                // Determinar qué elemento del menú se ha seleccionado
                when (menuItem.itemId) {
                    R.id.menu_reset -> {
                        // Crear un cuadro de diálogo para preguntar al usuario si realmente desea restablecer el récord
                        AlertDialog.Builder(this, R.style.EstiloDialogo)
                            .setTitle("¿Estás seguro?")
                            .setMessage("¿Realmente deseas restablecer el récord?")
                            .setPositiveButton("Sí") { _, _ ->
                                // Si el usuario hace clic en "Sí", borrar el registro de la tabla NClicker2
                                val db = dbHelper.writableDatabase
                                db.delete(SQLiteHelper.TABLE_NAME2, null, null)
                                db.close()
                                val toast = Toast.makeText(this, "El Récord se ha restablecido", Toast.LENGTH_SHORT)
                                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                                    setTextColor(ContextCompat.getColor(context, R.color.white))
                                    textSize = 16f
                                }
                                toast.show()
                                tvRecord.text = "0"
                            }
                            .setNegativeButton("No", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
            // Mostrar el PopupMenu
            popupMenu.show()
        }

        btnClick.setOnClickListener {
            btnClick.startAnimation(animacion)
            tvPoints.startAnimation(animacion2)
            val borderAnimationView = findViewById<LottieAnimationView>(R.id.animacion_circulo)
            borderAnimationView.speed = 4.0f
            borderAnimationView.setAnimation(R.raw.tap)
            borderAnimationView.playAnimation()
            btnClick.startAnimation(animacion)
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(30)
            }
            addPoint()
            if (!isRunning) startTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        // Detener el temporizador si está en ejecución
        if (isRunning) {
            timer?.cancel()
            isRunning = false
        }
    }

    override fun onStop() {
        super.onStop()
        // Detener el temporizador si está en ejecución
        if (isRunning) {
            timer?.cancel()
            isRunning = false
        }
    }

    override fun onResume() {
        super.onResume()
        val record = getRecord()
        if (record != null) {
            tvRecord.text = "$record"
        }
    }

    private fun addPoint() {
        puntos++
        tvPoints.text = puntos.toString()
    }

    private fun startTimer() {
        isRunning = true
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                tvTime.text = (timeLeft / 1000).toString()
                // Actualizar el progreso de la barra de progreso
                barraProgreso.progress = (timeLeft / 1000).toInt()
            }

            override fun onFinish() {
                isRunning = false
                tvTime.text = "0"
                checkRecord()
                // Mostrar el mensaje de alerta con la puntuación obtenida
                val score = puntos
                AlertDialog.Builder(this@FastTap, R.style.EstiloDialogo)
                    .setTitle("¡Tiempo agotado!")
                    .setMessage("Puntuación obtenida: $score")
                    .setPositiveButton("Cerrar") { _, _ ->
                        // Reiniciar el contador
                        puntos = 0
                        tvPoints.text = "0"
                        timeLeft = 15000
                    }
                    .setCancelable(false)
                    .show()
            }
        }.start()
    }

    private fun checkRecord() {
        val record = getRecord()
        if (record == null || puntos > record) {
            updateRecord()
            tvRecord.text = "Nuevo record: $puntos"
        }
    }

    private fun getRecord(): Int? {
        val cursor = db.query(
            TABLE_NAME2,
            arrayOf(COL_POINTS),
            null,
            null,
            null,
            null,
            "$COL_POINTS DESC",
            "1"
        )
        return if (cursor.moveToFirst()) cursor.getInt(0) else null
    }

    private fun updateRecord() {
        val values = ContentValues().apply {
            put(COL_POINTS, puntos)
        }
        if(db.update(TABLE_NAME2, values, null, null) == 0) {
            db.insert(TABLE_NAME2, null, values)
        }
    }

    companion object {
        const val COL_POINTS = "puntos"
    }
}