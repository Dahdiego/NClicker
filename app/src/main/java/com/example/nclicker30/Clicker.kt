package com.example.nclicker30

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color.*
import android.os.*
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView

class Clicker : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var contadorPuntos: TextView
    private lateinit var contadorPrestigio: TextView
    private lateinit var proximamejora: TextView
    private lateinit var tienda: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var textoNanos: TextView
    private lateinit var botonMenu: Button
    private lateinit var botonN: Button
    private lateinit var boton1: Button
    private lateinit var boton2: Button
    private lateinit var boton3: Button
    private lateinit var boton4: Button
    private lateinit var boton5: Button
    private lateinit var boton6: Button
    private lateinit var boton7: Button

    // Variables globales
    private var puntos: Int = 0
    private var puntosTotales: Int = 0
    private var prestigio: Int = 0
    private var multiplicador: Int = 1
    private var botonesDesbloqueados: Int = 0

    // Constantes para los puntos necesarios para desbloquear cada botón
    val PUNTOS_BOTON_1 = 500
    val PUNTOS_BOTON_2 = 1900
    val PUNTOS_BOTON_3 = 5000
    val PUNTOS_BOTON_4 = 10500
    val PUNTOS_BOTON_5 = 30500
    val PUNTOS_BOTON_6 = 80500
    val PUNTOS_BOTON_7 = 175000

    private lateinit var dbHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        drawerLayout = findViewById(R.id.drawer_layoutClicker)
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
        contadorPuntos = findViewById(R.id.tvcontadorPuntos)
        contadorPrestigio = findViewById(R.id.tvcontadorPrestigio)
        textoNanos = findViewById(R.id.tvNanos)
        proximamejora = findViewById(R.id.tvmejora)
        tienda = findViewById(R.id.tvlinea)
        botonN = findViewById(R.id.btN)
        botonMenu = findViewById(R.id.btmenu)
        boton2 = findViewById(R.id.bt2)
        boton3 = findViewById(R.id.bt3)
        boton4 = findViewById(R.id.bt4)
        boton5 = findViewById(R.id.bt5)
        boton1 = findViewById(R.id.bt1)
        boton6 = findViewById(R.id.bt6)
        boton7 = findViewById(R.id.bt7)

        // Crear una animación de escala
        val animacion3 = ValueAnimator.ofFloat(1.0f, 0.95f, 1.0f)
        animacion3.duration = 1500 // Duración de la animación en milisegundos
        animacion3.repeatCount = ValueAnimator.INFINITE // Repetir la animación indefinidamente
        animacion3.repeatMode = ValueAnimator.REVERSE // Invertir la animación después de cada ciclo

        // Actualizar la escala del botón en cada fotograma de la animación
        animacion3.addUpdateListener { valueAnimator ->
            val factorEscala = valueAnimator.animatedValue as Float
            botonN.scaleX = factorEscala
            botonN.scaleY = factorEscala
            proximamejora.scaleX = factorEscala
            proximamejora.scaleY = factorEscala
            tienda.scaleX = factorEscala
            tienda.scaleY = factorEscala
        }
        // Iniciar la animación
        animacion3.start()

        dbHelper = SQLiteHelper(this)
        cargarJuego()

        boton1.isEnabled = false
        boton2.isEnabled = false
        boton3.isEnabled = false
        boton4.isEnabled = false
        boton5.isEnabled = false
        boton6.isEnabled = false
        boton7.isEnabled = false

        botonMenu.setOnClickListener { view ->
            botonMenu.startAnimation(animacion)
            abrirMenu(view)
        }
        val navView: NavigationView = findViewById(R.id.nav_viewClicker)
        navView.setNavigationItemSelectedListener(this)

        botonN.setOnClickListener {
            val borderAnimationView = findViewById<LottieAnimationView>(R.id.animacion_spark)
            val borderAnimationView2 = findViewById<LottieAnimationView>(R.id.animacion_spark2)
            val borderAnimationView3 = findViewById<LottieAnimationView>(R.id.animacion_spark3)
            borderAnimationView.speed = 3.80f
            borderAnimationView2.speed = 3.80f
            borderAnimationView3.speed = 3.80f
            borderAnimationView.setAnimation(R.raw.sparks)
            borderAnimationView2.setAnimation(R.raw.sparks)
            borderAnimationView3.setAnimation(R.raw.sparks)
            borderAnimationView.playAnimation()
            borderAnimationView2.playAnimation()
            borderAnimationView3.playAnimation()
            botonN.startAnimation(animacion)
            contadorPuntos.startAnimation(animacion2)
            textoNanos.startAnimation(animacion2)
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(36, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(36)
            }
            //vibrar()
            // Aumentar los puntos gradualmente
            val valorInicial = puntos
            val valorInicial2 = puntosTotales
            val valorFinal = puntos + multiplicador
            val valorFinal2 = puntosTotales + multiplicador
            var incremento = 0
            if(multiplicador==1){
                incremento = multiplicador / 1
            }else if(multiplicador == 2){
                incremento = multiplicador/ 2
            }else if(multiplicador == 3){
                incremento = multiplicador / 3
            }else if(multiplicador == 7){
                incremento = multiplicador / 7
            } else if (multiplicador >= 10){
                incremento = multiplicador / 10
            }
            val tiempoEspera = 1L
            aumentarPuntos(valorInicial, valorFinal, incremento, tiempoEspera)
            aumentarPuntos2(valorInicial2, valorFinal2, incremento, tiempoEspera)
            // Sumar puntos
            // Actualizar la interfaz de usuario
            actualizarPuntos1()
            // Verificar si se desbloquea algún botón
            if (puntos >= 500 && botonesDesbloqueados == 0) {
                desbloquearBoton(1)
            }

            if (puntos >= 1900 && botonesDesbloqueados == 1) {
                desbloquearBoton(2)
            }

            if (puntos >= 5000 && botonesDesbloqueados == 2) {
                desbloquearBoton(3)
            }

            if (puntos >= 10500 && botonesDesbloqueados == 3) {
                desbloquearBoton(4)
            }

            if (puntos >= 30500 && botonesDesbloqueados == 4) {
                desbloquearBoton(5)
            }
            if (puntos >= 80500 && botonesDesbloqueados == 5) {
                desbloquearBoton(6)
            }
            if (puntos >= 175000 && botonesDesbloqueados == 6) {
                desbloquearBoton(7)
            }

            if (puntos >= 350000) {
                // Mostrar un AlertDialog informando que se ha llegado al máximo y que se va a hacer prestigio
                // Reiniciar puntos y multiplicador a su valor inicial
                // Incrementar contador de prestigio
                // Actualizar la interfaz de usuario
                hacerPrestigio()

            }
        }
        // Configurar el evento de click para los botones
        boton1.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_1) {
                boton1.startAnimation(animacion)
                canjearBoton(1)
            }
        }
        boton2.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_2 && botonesDesbloqueados == 1) {
                boton2.startAnimation(animacion)
                canjearBoton(2)
            }
        }
        boton3.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_3 && botonesDesbloqueados == 2) {
                boton3.startAnimation(animacion)
                canjearBoton(3)
            }
        }
        boton4.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_4 && botonesDesbloqueados == 3) {
                boton4.startAnimation(animacion)
                canjearBoton(4)
            }
        }
        boton5.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_5 && botonesDesbloqueados == 4) {
                boton5.startAnimation(animacion)
                canjearBoton(5)
            }
        }
        boton6.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_6 && botonesDesbloqueados == 5) {
                boton6.startAnimation(animacion)
                canjearBoton(6)
            }
        }
        boton7.setOnClickListener {
            if (puntos >= PUNTOS_BOTON_7 && botonesDesbloqueados == 6) {
                boton7.startAnimation(animacion)
                canjearBoton(7)
            }
        }

    }//FinDelOnCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clicker, menu)
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
                androidx.appcompat.app.AlertDialog.Builder(this, R.style.EstiloDialogo)
                    .setTitle("¿Estás seguro?")
                    .setMessage("¿Realmente deseas restablecer el récord?")
                    .setPositiveButton("Sí") { _, _ ->
                        // Si el usuario hace clic en "Sí", borrar el registro de la tabla NClicker2
                        val db = dbHelper.writableDatabase
                        db.delete(SQLiteHelper.TABLE_NAME2, null, null)
                        db.close()
                        contadorPuntos.text = "0"
                        contadorPrestigio.text = "0"
                        puntos = 0
                        puntosTotales = 0
                        prestigio = 0
                        multiplicador = 1
                        botonesDesbloqueados = 0
                        botonN.text = "N"
                        boton1.setBackgroundResource(R.drawable.bordes_redondos)
                        boton2.setBackgroundResource(R.drawable.bordes_redondos)
                        boton3.setBackgroundResource(R.drawable.bordes_redondos)
                        boton4.setBackgroundResource(R.drawable.bordes_redondos)
                        boton5.setBackgroundResource(R.drawable.bordes_redondos)
                        boton6.setBackgroundResource(R.drawable.bordes_redondos)
                        boton7.setBackgroundResource(R.drawable.bordes_redondos)
                        boton1.setTextColor(WHITE)
                        boton2.setTextColor(WHITE)
                        boton3.setTextColor(WHITE)
                        boton4.setTextColor(WHITE)
                        boton5.setTextColor(WHITE)
                        boton6.setTextColor(WHITE)
                        boton7.setTextColor(WHITE)
                        proximamejora.text = "Mejora : 500 puntos"
                        val toast = Toast.makeText(this, "El Récord se ha restablecido", Toast.LENGTH_SHORT)
                        toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                        toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                            setTextColor(ContextCompat.getColor(context, R.color.white))
                            textSize = 16f
                        }
                        toast.show()
                    }
                    .setNegativeButton("No", null)
                    .show()
                true

            }
            R.id.iNanosT -> {
                // Acción al seleccionar la opción 2
                androidx.appcompat.app.AlertDialog.Builder(this@Clicker, R.style.EstiloDialogo)
                    .setTitle("Nanos Totales")
                    .setMessage("$puntosTotales")
                    .setPositiveButton("Cerrar") { _, _ ->}
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
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layoutClicker)
        drawerLayout.openDrawer(GravityCompat.START)
    }


    // Función recursiva para aumentar gradualmente los puntos
    private fun aumentarPuntos(valorActual: Int, valorFinal: Int, incremento: Int, tiempoEspera: Long) {
        // Verificar si se ha alcanzado el valor final
        if (valorActual >= valorFinal) {
            puntos = valorFinal
            // Actualizar la interfaz de usuario
            actualizarPuntos1()
            return
        }

        // Aumentar el valor actual en incrementos pequeños
        puntos = valorActual + incremento

        // Actualizar la interfaz de usuario
        actualizarPuntos1()

        // Esperar un tiempo antes de llamar a la función de nuevo
        Handler().postDelayed({
            aumentarPuntos(puntos, valorFinal, incremento, tiempoEspera)
        }, tiempoEspera)
    }
    private fun aumentarPuntos2(valorActual2: Int, valorFinal2: Int, incremento: Int, tiempoEspera: Long) {
        // Verificar si se ha alcanzado el valor final
        if (valorActual2 >= valorFinal2) {
            puntosTotales = valorFinal2
            return
        }

        // Aumentar el valor actual en incrementos pequeños
        puntosTotales = valorActual2 + incremento


        // Esperar un tiempo antes de llamar a la función de nuevo
        Handler().postDelayed({
            aumentarPuntos(puntosTotales, valorFinal2, incremento, tiempoEspera)
        }, tiempoEspera)
    }

    // Definir funciones
    private fun actualizarPuntos1() {
        // Código para actualizar los puntos en la interfaz de usuario
        contadorPuntos.text = "$puntos"
    }

    private fun actualizarPrestigio1() {
        // Código para actualizar el contador de prestigio en la interfaz de usuario
        contadorPrestigio.text = "$prestigio"
    }


    // Función para mostrar el AlertDialog y realizar las acciones necesarias para el prestigio
    private fun hacerPrestigio() {
        val alertDialog = AlertDialog.Builder(this, R.style.EstiloDialogo)
        alertDialog.setTitle("¡Felicidades!")
        alertDialog.setMessage("Has alcanzado el puntaje máximo. Se va a hacer prestigio y volverás a 0")
        alertDialog.setPositiveButton("Cerrar") { _, _ ->

            // Incrementar contador de prestigio
            prestigio++
            contadorPuntos.text = "0"
            contadorPrestigio.text = "$prestigio"
            puntos = 0
            multiplicador = 2
            botonesDesbloqueados = 0
            botonN.text = "N"
            boton1.setBackgroundResource(R.drawable.bordes_redondos)
            boton2.setBackgroundResource(R.drawable.bordes_redondos)
            boton3.setBackgroundResource(R.drawable.bordes_redondos)
            boton4.setBackgroundResource(R.drawable.bordes_redondos)
            boton5.setBackgroundResource(R.drawable.bordes_redondos)
            boton6.setBackgroundResource(R.drawable.bordes_redondos)
            boton7.setBackgroundResource(R.drawable.bordes_redondos)
            boton1.setTextColor(WHITE)
            boton2.setTextColor(WHITE)
            boton3.setTextColor(WHITE)
            boton4.setTextColor(WHITE)
            boton5.setTextColor(WHITE)
            boton6.setTextColor(WHITE)
            boton7.setTextColor(WHITE)
            proximamejora.text = "Mejora : 500 puntos"

            // Actualizar la interfaz de usuario
            actualizarPuntos1()
            actualizarPrestigio1()

        }
        alertDialog.show()
    }

    private fun desbloquearBoton(numBoton: Int) {
        // Código para desbloquear el botón correspondiente en la interfaz de usuario
        // Actualizar la variable botonesDesbloqueados

        // Actualizar la interfaz de usuario
        when (numBoton) {
            1 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x3", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton1.isEnabled = true
                boton1.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton1.text = "Canjear"
                boton1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            2 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x7", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton2.isEnabled = true
                boton2.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton2.text = "Canjear"
                boton2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            3 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x16", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton3.isEnabled = true
                boton3.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton3.text = "Canjear"
                boton3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            4 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x34", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton4.isEnabled = true
                boton4.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton4.text = "Canjear"
                boton4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            5 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x72", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton5.isEnabled = true
                boton5.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton5.text = "Canjear"
                boton5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            6 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x153", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton6.isEnabled = true
                boton6.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton6.text = "Canjear"
                boton6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            7 -> {
                val toast = Toast.makeText(this, "Se ha desbloqueado el multiplicador x285", Toast.LENGTH_SHORT)
                toast.view?.setBackgroundResource(R.drawable.bordes_redondos)
                toast.view?.findViewById<TextView>(android.R.id.message)?.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    textSize = 16f
                }
                toast.show()
                boton7.isEnabled = true
                boton7.setBackgroundResource(R.drawable.bordes_redondosverdes)
                boton7.text = "Canjear"
                boton7.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            else -> {
                // No hacer nada si se pasa un valor inválido
            }
        }
    }

    // Función para canjear una mejora de un botón y actualizar la interfaz de usuario
    private fun canjearBoton(numBoton: Int) {
        when(numBoton) {
            1 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_1
                // Actualizar el multiplicador
                multiplicador = 3
                boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 1900 puntos"
                boton1.text = "x3"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton1.isEnabled = false
                // Desbloquear el siguiente botón si es necesario
                //if (puntos >= PUNTOS_BOTON_2 && botonesDesbloqueados < 2) {
                //   desbloquearBoton(2)
                // }
            }
            2 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_2
                // Actualizar el multiplicador
                multiplicador = 7
                boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 5000 puntos"
                boton2.text = "x7"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton2.isEnabled = false
                // Desbloquear el siguiente botón si es necesario
                //if (puntos >= PUNTOS_BOTON_3 && botonesDesbloqueados < 3) {
                //    desbloquearBoton(3)
                //}
            }
            3 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_3
                // Actualizar el multiplicador
                multiplicador = 16
                boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 10500 puntos"
                boton3.text = "x16"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton3.isEnabled = false
                // Desbloquear el siguiente botón si es necesario
                //if (puntos >= PUNTOS_BOTON_4 && botonesDesbloqueados < 4) {
                //   desbloquearBoton(4)
                //}
            }
            4 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_4
                // Actualizar el multiplicador
                multiplicador = 34
                boton4.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 30500 puntos"
                boton4.text = "x34"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton4.isEnabled = false
                // Desbloquear el siguiente botón si es necesario
                //if (puntos >= PUNTOS_BOTON_5 && botonesDesbloqueados < 5) {
                //   desbloquearBoton(5)
                //}
            }
            5 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_5
                // Actualizar el multiplicador
                multiplicador = 72
                boton5.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 80500 puntos"
                boton5.text = "x72"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton5.isEnabled = false
                // Desbloquear el siguiente botón si es necesario
                // if (puntos >= PUNTOS_BOTON_6 && botonesDesbloqueados < 6) {
                //    desbloquearBoton(6)
                // }
            }
            6 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_6
                // Actualizar el multiplicador
                multiplicador = 153
                boton6.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Mejora : 175000 puntos"
                boton6.text = "x153"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton6.isEnabled = false
            }
            7 -> {
                // Restar los puntos necesarios para canjear el botón
                puntos -= PUNTOS_BOTON_7
                // Actualizar el multiplicador
                multiplicador = 285
                boton7.setBackgroundResource(R.drawable.bordes_redondosgrises)
                proximamejora.text = "Prestigio : 350000 puntos"
                boton7.text = "x285"
                botonN.text = "N x$multiplicador"
                botonesDesbloqueados ++
                boton7.isEnabled = false
            }
        }
        // Actualizar la interfaz de usuario
        actualizarPuntos1()
    }
    /*
        fun vibrar(){
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            // Definir una duración de vibración de 40 milisegundos
            val duration = 40L

            // Definir un nivel de intensidad de 100
            val intensity = 100

            // Crear un patrón de vibración con un solo tiempo de vibración
            val pattern = longArrayOf(duration)

            // Comprobar si el dispositivo admite la vibración
            if (vibrator.hasVibrator()) {
                // Comprobar si el dispositivo está ejecutando la versión de Android Oreo o superior
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // Crear un objeto VibrationEffect con la duración, el patrón y la intensidad especificados
                    val vibrationEffect = VibrationEffect.createWaveform(pattern, intensity)

                    // Hacer que el dispositivo vibre con el efecto de vibración especificado
                    vibrator.vibrate(vibrationEffect)
                } else {
                    // Hacer que el dispositivo vibre con el patrón de vibración y la intensidad especificados
                    vibrator.vibrate(pattern, -1)
                }
            }
        }
    */

    override fun onPause() {
        super.onPause()
        guardar()
    }

    private fun cargarJuego() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(SQLiteHelper.TABLE_NAME, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            puntos = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMNA_PUNTOS))
            prestigio = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMNA_PRESTIGIO))
            botonesDesbloqueados = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMNA_BOTONES))
            multiplicador = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMNA_MULTIPLICADOR))
        }
        cursor.close()
        db.close()
        actualizarPuntos1()
        actualizarPrestigio1()

        if (multiplicador == 3){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 1900 puntos"
        }else if (multiplicador == 7){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 5000 puntos"
        }else if (multiplicador == 16){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            boton3.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 10500 puntos"
        }else if (multiplicador == 34){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton4.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            boton3.isEnabled = false
            boton4.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 30500 puntos"
        }else if (multiplicador == 72){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton4.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton5.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            boton3.isEnabled = false
            boton4.isEnabled = false
            boton5.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 80500 puntos"
        }else if (multiplicador == 153){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton4.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton5.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton6.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            boton3.isEnabled = false
            boton4.isEnabled = false
            boton5.isEnabled = false
            boton6.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Mejora : 175000 puntos"
        }else if (multiplicador == 285){
            boton1.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton2.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton3.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton4.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton5.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton6.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton7.setBackgroundResource(R.drawable.bordes_redondosgrises)
            boton1.isEnabled = false
            boton2.isEnabled = false
            boton3.isEnabled = false
            boton4.isEnabled = false
            boton5.isEnabled = false
            boton6.isEnabled = false
            boton7.isEnabled = false
            botonN.text = "N x$multiplicador"
            proximamejora.text = "Prestigio : 350000 puntos"
        }
    }

    private fun guardar() {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(SQLiteHelper.COLUMNA_PUNTOS, puntos)
        values.put(SQLiteHelper.COLUMNA_PRESTIGIO, prestigio)
        values.put(SQLiteHelper.COLUMNA_BOTONES, botonesDesbloqueados)
        values.put(SQLiteHelper.COLUMNA_MULTIPLICADOR, multiplicador)
        if(db.update(SQLiteHelper.TABLE_NAME, values, null, null) == 0) {
            db.insert(SQLiteHelper.TABLE_NAME, null, values)
        }
        db.close()
    }
}