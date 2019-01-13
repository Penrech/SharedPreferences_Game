package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 * Esta clase tiene como parámetro
 * @property id que es el id de la dificultad de las preguntas
 * @property name que es el nombre de la dificultad de las preguntas
 * @property temas que es una lista de
 * @see PreguntasTema
 *
 * Todas las propiedades de esta clase están inicializados para emular a un constructor vacio necesario para servir
 * de modelo para la BBDD de Firebase
 *
 * */
class PreguntasDificultad(var id: Int? = null,
                          var name: String? = null,
                          var temas: MutableList<PreguntasTema> = mutableListOf()) {}