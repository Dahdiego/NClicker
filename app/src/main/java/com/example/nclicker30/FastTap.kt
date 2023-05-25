package com.example.nclicker30

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Context
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.*
import android.util.Log
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
import com.airbnb.lottie.LottieAnimationView
import com.example.nclicker30.SQLiteHelper.Companion.TABLE_NAME2
import com.google.android.material.navigation.NavigationView


class FastTap : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tvPoints: TextView
    private lateinit var tvTime: TextView
    private lateinit var btnClick: Button
    private lateinit var tvRecord: TextView
    private lateinit var botonMenu: Button
    private lateinit var drawerLayout: DrawerLayout


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
        drawerLayout = findViewById(R.id.drawer_layoutFastTap)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val animacion = AnimationUtils.loadAnimation(this, R.anim.presionar_boton)
        val animacion2 = AnimationUtils.loadAnimation(this, R.anim.anim_textview)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        tvPoints = findViewById(R.id.tvPoints)
        tvTime = findViewById(R.id.tvTime)
        btnClick = findViewById(R.id.btN)
        tvRecord = findViewById(R.id.tvRecord)
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

        botonMenu.setOnClickListener { view ->
            botonMenu.startAnimation(animacion)
            abrirMenu(view)
        }
        val navView: NavigationView = findViewById(R.id.nav_viewFastTap)
        navView.setNavigationItemSelectedListener(this)


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
    }//Fin del oncreate

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
                        db.delete(SQLiteHelper.TABLE_NAME2, null, null)
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
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layoutFastTap)
        drawerLayout.openDrawer(GravityCompat.START)
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