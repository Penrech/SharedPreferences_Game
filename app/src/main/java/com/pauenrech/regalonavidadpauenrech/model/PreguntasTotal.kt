package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 * Esta clase tiene como parámetro
 * @property totalPreguntas que es una lista de
 * @see PreguntasDificultad
 *
 * Esta clase es una lista de
 * @see PreguntasTotal , y está creada para seguir el modelo implementado en la BBDD Firebase. Por este mismo motivo, tiene un
 * constructor con su propiedad inicializada para emular a un constructor vacio necesario para obtener datos de firebase
 *
 * */
class PreguntasTotal(var totalPreguntas: MutableList<PreguntasDificultad> = mutableListOf()) {}