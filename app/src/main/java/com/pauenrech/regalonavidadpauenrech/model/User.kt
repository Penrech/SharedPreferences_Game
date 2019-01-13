package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 * Esta clase implementa el modelo de básico de usuario, donde se guardan los datos de un usuario en concreto.
 * Todas su propiedades están inicializadas de alguna manera para crear un constructor sin parámetros de entrada
 * obligatorios. Esto es necesario para rellenar esta clase con datos de Firebase en caso de que fuera necesario
 *
 * Cuenta con las siguientes propiedades:
 * @property uid que representa la id única del usuario, se obtiene de la base de datos Firebase.
 * @property nickname que representa el nickname del usuario, este es también es único en la base de datos Firebase
 * para ello se introduce la siguiente propiedad
 * @property nicknameLowerCase que guarda el nickname to_do en minisculas para poder hacer comprobaciones de este con
 * Firebase más correctamente. UsuarioPruebas != usuariopruebas -> usuariopruebas == usuariopruebas
 * @property dificultad que representa que dificultad a seleccionado el usuario, es de la clase Int ya que de esta
 * forma es más sencillo determinar la dificultad en cada momento y en las diferentes situaciones
 * @property puntuacion que representa la puntuación total del usuario
 * @property ranking que representa el ranking del usuario respecto a otros usuarios de Firebase (No implementado actualmente)
 * @property temas que representa una lista de listas de la clase
 * @see ThemeScore , Esta lista tiene de longitud fija tiene 3 elementos, los 3 elementos que representan la dificultad
 *
 */
class User(
    var uid: String? = null,
    var nickname: String = "Username",
    var nicknameLowerCase: String = "username",
    var dificultad: Int = 0,
    var puntuacion: Int = 0,
    var ranking: Int = -1,
    var temas: List<MutableList<ThemeScore>> = listOf(mutableListOf(), mutableListOf(),
        mutableListOf()))
{
    /**
     *
     * @see listaTemas es un mapa clave valor que almacena la id y el nombre de cada uno de los temas del usuario,
     * independientemente de si es de una dificultad o de otra. Esto es necesario porque simplifica mucho la tarea
     * de conocer cuantos temas tiene el usuario para igualarlo a la lista de temas obviando la duplicidad por dificultades
     * que incluye
     * @see temas
     *
     * @see numeroDeTemas es una variable que guarda el número de temas
     *
     * */
    private var listaTemas: MutableMap<String,String> = HashMap()
    var numeroDeTemas : Int = 0

    /**
     *
     * En la inicialización de la clase se rellena la
     * @see listaTemas con los temas recibidos del usuario guardado en sharedPreferences
     * Para ello se recorre la lista cogiendo la longitud de una de sus listas, la primera de ellas
     * que representa la dificultad facil. Esto es así porque las tres listas, facil, medio y dificl tienen siempre
     * la misma longitud
     *
     * Seguidamente se actualiza la variable
     * @see numeroDeTemas
     *
     * */
    init {
        temas[DificultadEnum.FACIL.value].forEach {
            listaTemas.put(it.id,it.name)
        }
        numeroDeTemas = listaTemas.size
    }

    /**
     *
     * @see DificultadEnum es una clase que representa de forma textual los valores de dificultad
     * que son del tipo Int.
     * Para ello recibe
     * @property value y devuelve el string asociado a este
     *
     * */
    enum class DificultadEnum (val value: Int) {
        FACIL(0),
        MEDIO(1),
        DIFICIL(2)
    }

    /**
     *
     * @see deleteTema es un método que permite eliminar temas de la lista de temas del usuario. Cada vez que se obtiene
     * la lista de temas de la base de datos, es necesario actualizar la lista de temas del usuario. Los temas de la base
     * de datos son los relevantes, por ello el usuario debe tener exactamente el mismo número de temas, con las mismas
     * ids y los mismos nombres
     * Recibe
     * @property _webTemas que representa la lista de temas que llegán de la base de datos y es de la clase
     * @see TemasList
     *
     * */
    fun deleteTema(_webTemas: TemasList){
        /**
         *
         * @param willBeDeleted es una lista mutable vacia de String, almacenará las ids de los temas que de no
         * coincidir con los de
         * @property _webTemas serán eliminados
         *
         * Se recorre
         * @see listaTemas , lista de temas locales, con un loop
         * Por cada elemento se crea un filtro
         * @param filter que contiene los temas que coinciden, es decir, su id está tanto en la BBDD local como en la BBDD
         * Firebase
         * Se comprueba si este parámetro está vacio, lo que significa que el
         * @param temalocal en cuestión no está en dentro de los
         * @property _webTemas por lo que debe ser eliminado, asi que se añade a la lista willBeDeleted
         *
         * Se crea la variable
         * @param indexToRemoveFromMap que es una lista de temas que serán eliminados de
         * @see listaTemas
         *
         * Seguidamente se recorre la lista
         * @see temas
         * Por cada dificultad se crea una lista de
         * @see ThemeScore con el nombre
         * @param indexToDelete
         * Se recorren los temas de cada dificultad y se comprueba si la id de cada uno de ellos está dentro
         * de la lista de temas a eliminar
         * @see willBeDeleted, si es el caso se añade a la lista
         * @see indexToBeDelete
         *
         * Se eliminan los temas de cada dificultad que estén dentro de la lista indexToBeDeleted con el método
         * @see MutableList.removeAll con la lista indexToBeDeleted como parámetro
         * También se iguala
         * @see indexToRemoveFromMap con
         * @see indexToDelete ya que los indices a borrar en cada una de las dificultades son los mismos
         *
         * Finalmente se recorre la lista de
         * @see indexToRemoveFromMap y se eliminan las coincidencias con
         * @see listaTemas con el método de
         * @see MutableList.remove pasando como parámetro la id de cada tema a eliminar
         * Se actualiza
         * @see numeroDeTemas
         *
         * */
        val willBeDeleted = mutableListOf<String>()

        listaTemas.forEach { temalocal ->
            val filter = _webTemas.temas.filter { it.id == temalocal.key }
            if (filter.isEmpty())
                willBeDeleted.add(temalocal.key)
        }

        var indexToRemoveFromMap = mutableListOf<ThemeScore>()
        temas.forEach {dificultad ->
            val indexToDelete = mutableListOf<ThemeScore>()
            dificultad.forEach{ themeScore ->
                if (willBeDeleted.contains(themeScore.id)){
                   indexToDelete.add(themeScore)
                }
            }

            dificultad.removeAll(indexToDelete)
            indexToRemoveFromMap = indexToDelete
        }
        indexToRemoveFromMap.forEach {
            listaTemas.remove(it.id)
        }
        numeroDeTemas = listaTemas.size
    }

    /**
     *
     * @see modifyTemaScore recibe como parámetro
     * @property id que es la id de un tema
     * @property score que es la nueva puntuación de un tema
     *
     * Este método modifica la puntuación de un tema en una dificultad concreta
     *
     * */
    fun modifyTemaScore(id: String, score: Int){

        /**
         *
         * Se accede a
         * @see temas en la posición determinada por
         * @see dificultad y se filtra para obtener el tema que corresponde a
         * @property id
         *
         * Seguidamente se coje el primer valor de la
         * @see List resultante. Se espera solo un resultado, que se guarda en el indice 0
         *
         * Como resultado tenemos el
         * @see ThemeScore en cuestión al que se le cambia su propiedad
         * @see ThemeScore.score con la obtenida en
         * @property score
         *
         * */
        temas[dificultad].filter { it.id == id }[0].score = score
    }

    /**
     *
     * @see getTemaScore recibe como parámetro
     * @property id , la cual es la id del tema del que se quiere obtener la puntuación
     *
     * Este método obtiene la puntuación de un determinado tema en función de su id y dificultad
     * @return devuelve un [Int] que corresponde al score de ese determinado tema
     *
     * */
    fun getTemaScore(id: String): Int{

        /**
         *
         * Se accede a
         * @see temas en el indice marcado por
         * @see dificultad y se filtra por id para obtener el tema que coincida con
         * @property id
         *
         *Seguidamente se coje el primer valor de la
         * @see List resultante. Se espera solo un resultado, que se guarda en el indice 0
         * y se envia como resultado
         *
         * */
        return temas[dificultad].filter { it.id == id }[0].score
    }

    /**
     *
     * @see addTemaLista recibe como parámetros:
     * @property name que representa el nombre de un tema
     * @property id que representa la ide de un tema
     *
     * Este método añade un tema nuevo a la lista de temas del usuario o lo actualiza su nombre si ya está en ella
     *
     * */
    fun addTemaLista(name: String, id: String){

        /**
         *
         * Primero se comprueba si
         * @see listaTemas contiene
         * @property id del nuevo tema
         *
         * Si no es así, el nuevo se añade al mapa
         * @see listaTemas con su id como key y su nombre como valor
         * Además, se recorre la lista
         * @see temas y se añade dicho tema a cada una de las dificultades con un valor de puntuación inicial de 0
         * Despues se actualiza la variable
         * @see numeroDeTemas
         *
         * Si el tema ya está en la lista de temas del usuario
         * Se comprueba si el nombre del tema incluido en
         * @see listaTemas , su valor, es igual al del nuevo tema recibido a través de
         * @property name
         * Si esta igualdad no es correcta, es decir si el tema difiere, se actualiza el tema local con el tema obtenido en
         * @property name
         * Para ello se actualiza el mapa
         * @see listaTemas y seguidamente se recorre
         * @see temas para modificar el nombre del tema en cada una de las dificultades
         *
         * */
        if (!listaTemas.contains(id)) {
            listaTemas.put(id,name)
            temas.forEachIndexed { index, it ->
                it.add(ThemeScore(name, id, index,0))
            }
            numeroDeTemas = listaTemas.size
        }
        else{
            if(listaTemas[id] != name){
                listaTemas[id] = name
                temas.forEach { mutableList ->
                    mutableList.forEach {
                        if (it.id == id){
                            it.name = name
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * Esta clase se incluye dentro de la clase
     * @see User por que están ampliamente relacionadas y para diferenciarla de la otra clase referente a Temas.
     * Los temas de clase
     * @see Tema que hay dentro de la lista de
     * @see TemasList no son el mismo tipo de temas que los temas
     * @see ThemeScore que hay dentro de la lista de
     * @see User.temas
     *
     * Estos temas tienen los siguientes parámetros
     * @property name El nombre del tema
     * @property id la id única del tema, esta id coincide con la id de los
     * @see Tema de
     * @see TemasList , todos los temas son los mismos aunque las propiedades de sus modelos sean diferentes
     *
     *@property dificultadid el id de la dificultad de este tema en concreto dentro de una
     * dificultad en concreto . Facil: 0, Medio: 1, Dificil: 2
     *@property score la puntuación de cada tema dentro de una dificultad en concreto.
     *
     * */
    class ThemeScore(
        var name: String,
        var id: String,
        var dificultadid: Int,
        var score: Int)
}