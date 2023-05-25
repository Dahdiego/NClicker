package com.example.nclicker30

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Juego"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "NClicker"
        const val TABLE_NAME2 = "NClicker2"
        const val TABLE_NAME3 = "NClicker3"
        const val COLUMNA_PUNTOS = "puntos"
        const val COLUMNA_ID = "id"
        const val COLUMNA_MULTIPLICADOR = "multiplicador"
        const val COLUMNA_PRESTIGIO = "prestigio"
        const val COLUMNA_BOTONES = "botones"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val Tabla1 = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMNA_PUNTOS INTEGER,"
                + "$COLUMNA_PRESTIGIO INTEGER,"
                + "$COLUMNA_BOTONES INTEGER,"
                + "$COLUMNA_MULTIPLICADOR INTEGER" + ")")
        db?.execSQL(Tabla1)

        val Tabla2 = ("CREATE TABLE $TABLE_NAME2 ("
                + "$COLUMNA_ID INTEGER,"
                + "$COLUMNA_PUNTOS INTEGER" + ")")
        db?.execSQL(Tabla2)

        val Tabla3 = ("CREATE TABLE $TABLE_NAME3 ("
                + "$COLUMNA_ID INTEGER,"
                + "$COLUMNA_PUNTOS INTEGER" + ")")
        db?.execSQL(Tabla3)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME2")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME3")
        onCreate(db)
    }
}