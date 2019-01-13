package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 * Esta clase es la clase base de Tema, y al igual que los demás modelos tiene un constructor con propiedades ya
 * inicializadas para emular a un constructor vacio necesario para obtener datos de Firebase
 * Incluye las siguientes propiedades
 * @property name El nombre del tema
 * @property id La id del tema, coincide con la id de el modelo de datos de los temas de usuario
 * @see User.ThemeScore
 * @property colorStart es el color inicial del gradiente que se verá en pantalla cuando se este jugando en este tema
 * @property colorEnd es el color final del del gradiente que se verá en pantalla cuando se este jugando en este tema
 *
 * */
class Tema(
    var name: String = "",
    var id: String = "",
    var colorStart: String = "",
    var colorEnd: String = "") {}