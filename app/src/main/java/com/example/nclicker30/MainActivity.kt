package com.example.nclicker30


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.nclicker30.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var botonSonido: MediaPlayer
    private lateinit var sonidoFondo: MediaPlayer
    private lateinit var myReceiver: MyReceiver
    private lateinit var drawerLayout: DrawerLayout
    private var isForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Configuracion ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val borderAnimationView = findViewById<LottieAnimationView>(R.id.animacion_rotar)
        val borderAnimationView2 = findViewById<LottieAnimationView>(R.id.animacion_rotar2)
        borderAnimationView.setAnimation(R.raw.rotacion)
        borderAnimationView2.setAnimation(R.raw.rotacion)
        borderAnimationView.speed = 0.45f
        borderAnimationView2.speed = 0.45f
        borderAnimationView.playAnimation()
        borderAnimationView2.playAnimation()

        val btmenu = findViewById<Button>(R.id.btmenu)
        btmenu.setOnClickListener { view ->
            abrirMenu(view)
        }

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        //Para cambiar el color del texto del item de un menu.
        //navView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        //Para cambiar el color del icono del item de un menu.
        //navView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        //Para cambiar el color del fondo del menu.
        //navView.setBackgroundColor(getResources().getColor(R.color.colorfondo));

        val animacion = AnimationUtils.loadAnimation(this, R.anim.presionar_boton)
        sonidoFondo = MediaPlayer.create(this, R.raw.musica_fondo)
        botonSonido = MediaPlayer.create(this, R.raw.clickbotonmenu)
        // Crea el objeto MyReceiver y lo registra
        myReceiver = MyReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(myReceiver, intentFilter)

        sonidoFondo.isLooping = true // Reproduce la música de forma continua
        sonidoFondo.start() // Inicia la reproducción
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
/**
        Animacion para vibrar

            // Crear animación para botón 1
        val anim1 = ObjectAnimator.ofFloat(binding.btNclicker, "rotation", -0.5f, 0.5f)
        anim1.duration = 20
        anim1.repeatCount = ValueAnimator.INFINITE
        anim1.repeatMode = ObjectAnimator.REVERSE

            // Crear animación para botón 2
        val anim2 = ObjectAnimator.ofFloat(binding.btFastTap, "rotation", -0.5f, 0.5f)
        anim2.duration = 20
        anim2.repeatCount = ValueAnimator.INFINITE
        anim2.repeatMode = ObjectAnimator.REVERSE

            // Crear animación para botón 3
        val anim3 = ObjectAnimator.ofFloat(binding.btTimer, "rotation", -0.5f, 0.5f)
        anim3.duration = 20
        anim3.repeatCount = ValueAnimator.INFINITE
        anim3.repeatMode = ObjectAnimator.REVERSE

            // Ejecutar las animaciones simultáneamente
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(anim1, anim2, anim3)
        animatorSet.start()
**/

            // Crear una animación de escala
        val animacion2 = ValueAnimator.ofFloat(1.0f, 0.95f, 1.0f)
        animacion2.duration = 1500 // Duración de la animación en milisegundos
        animacion2.repeatCount = ValueAnimator.INFINITE // Repetir la animación indefinidamente
        animacion2.repeatMode = ValueAnimator.REVERSE // Invertir la animación después de cada ciclo

            // Actualizar la escala del botón en cada fotograma de la animación
        animacion2.addUpdateListener { valueAnimator ->
            val factorEscala = valueAnimator.animatedValue as Float
            binding.btNclicker.scaleX = factorEscala
            binding.btNclicker.scaleY = factorEscala
            binding.btFastTap.scaleX = factorEscala
            binding.btFastTap.scaleY = factorEscala
            binding.btTimer.scaleX = factorEscala
            binding.btTimer.scaleY = factorEscala
            binding.tvLogo.scaleX = factorEscala
            binding.tvLogo.scaleY = factorEscala
        }
            // Iniciar la animación
        animacion2.start()

        binding.btNclicker.setOnClickListener(){
            binding.btNclicker.startAnimation(animacion)
            botonSonido.start() // Inicia la reproducción
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            val intent = Intent (this, Clicker::class.java)
            startActivity(intent)
        }
        binding.btinfo1.setOnClickListener(){
            binding.btinfo1.startAnimation(animacion)
            AlertDialog.Builder(this@MainActivity, R.style.EstiloDialogo)
                .setTitle("Información")
                .setMessage("El Objetivo es conseguir el mayor número de puntos, al llegar a 100mil, se hará prestigio y volverás a 0. " +
                        "Hasta donde serás capaz de llegar.")
                .setPositiveButton("Cerrar") { _, _ ->
                }
                .setCancelable(false)
                .show()
        }
        binding.btFastTap.setOnClickListener(){
            binding.btFastTap.startAnimation(animacion)
            botonSonido.start() // Inicia la reproducción
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            val intent = Intent (this, FastTap::class.java)
            startActivity(intent)
        }
        binding.btinfo2.setOnClickListener(){
            binding.btinfo2.startAnimation(animacion)
            AlertDialog.Builder(this@MainActivity, R.style.EstiloDialogo)
                .setTitle("Información")
                .setMessage("El Objetivo es conseguir el mayor número de puntos antes de que el contador llegue a 0. " +
                        "¿Serás el más rápido de todos?")
                .setPositiveButton("Cerrar") { _, _ ->
                }
                .setCancelable(false)
                .show()
        }
        binding.btTimer.setOnClickListener(){
            binding.btTimer.startAnimation(animacion)
            botonSonido.start() // Inicia la reproducción
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            val intent = Intent (this, Timer::class.java)
            startActivity(intent)
        }
        binding.btinfo3.setOnClickListener(){
            binding.btinfo3.startAnimation(animacion)
            AlertDialog.Builder(this@MainActivity, R.style.EstiloDialogo)
                .setTitle("Información")
                .setMessage("El Objetivo es conseguir el mayor número de veces en las que puedas parar el cronómetro entre 4:95 y 5:05, cada vez que falles 1 vida menos, tienes 3 vidas." +
                        "¿Tendrás los reflejos necesarios?")
                .setPositiveButton("Cerrar") { _, _ ->
                }
                .setCancelable(false)
                .show()
        }
    } // Fin onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lateral, menu)
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
            R.id.imusica -> {
                // Acción al seleccionar la opción 1
                // Cambiar el icono del elemento de menú según su estado actual
                if (sonidoFondo.isPlaying) {
                    sonidoFondo.pause()
                    item.setIcon(R.drawable.musicaoff)
                } else {
                    sonidoFondo.start()
                    item.setIcon(R.drawable.musicaon1)
                }
            }
            R.id.icerrar -> {
                // Acción al seleccionar la opción 2
                finish()
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
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.openDrawer(GravityCompat.START)
    }
    override fun onDestroy() {
        super.onDestroy()
        botonSonido.stop()
        botonSonido.release()
        sonidoFondo.stop()
        sonidoFondo.release()

        // Desregistra el objeto MyReceiver
        unregisterReceiver(myReceiver)
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_SCREEN_OFF) {
                // Detiene la música
                sonidoFondo.stop()
            }
        }
    }
}