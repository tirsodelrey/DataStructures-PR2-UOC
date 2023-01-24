# DS - PR2 - Tirso del Rey Gumb - 2022/23

## Introducción

Durante esta segunda entrega de la práctica de la asignatura de diseño de estructuras de datos de la UOC, se implementaron una serie de cambios en algunas de las estructuras de datos utilizadas en la entrega anterior, que analizaremos a continuación, para lo que se utilizaron nuevos TAD (HashTable, DictionaryAVLImpl, PriorityQueue) estudiados en la segunda parte del semestre y presentes en la biblioteca de TAD de la asignatura.
Para estas nuevas estructuras se reutilizaron algunos de los modelos utilizados en la práctica anterior y también se añadieron nuevos objetos mediante las nuevas clases Attender, Worker y Role.

También se incluyeron nuevas excepciones que permitiesen gestionar posibles errores que puedan ocurrir durante la interacción con las estructuras de datos implementadas. Estas excepciones se implementaron utilizando como guía los test proporcionados, es decir, utilizando la metodología `Test Driven Development` (TDD). 

El TDD también se utilizó para modificar las estructuras de datos y añadir nuevas en base a las especificaciones de los test.

No se incluyeron test adicionales ya que se utilizaron los TAD ya presentes en la biblioteca de la asignatura, y se consideraró que los test proporcionados ya comprueban el correcto funcionamiento de estos.

Se analiza en más detalle a continuación los cambios realizados respecto a la PR1:


### **Modelos**

Se describirán las modificaciones respecto a la anterior entrega:

Se implementaron los modelos `Worker`, `Attender`, y `Role`. Se utilizan como abstracciones de objetos necesarios en la implementación de las nuevas funcionalidades y estructuras de datos de la PR2. Es interesante destacar los siguiente métodos o estructuras implementados por su importancia en el funcionamiento global de la aplicación:
* Role: Contiene una 'LinkedList<Worker>'de trabajadores y métodos que permiten obtener datos de estos, como se especifica en la PEC2.
* Player: Cuenta con un atributo de lista encadadenada `LinkedList<SportEvent>` para almacenar los eventos deportivos en los que participa, como se definió en la solución de la PEC1. Implementa un método auxuliar `isInEvent` que permite determinar si el jugador participa en el evento pasado por parámetro.
* SportEvent: Modela el evento deportivo. Contiene un nuevo TAD `PriorityQueue<Enrollment> substitutes` , una cola con prioridad que permite almacenar las inscripciones de los jugadores suplentes siguiendo un orden basado en criterios preestablecidos indicados en la solución de la PEC2. Almacenará también los trabajadores según lo descrito en la PEC2 mediante una lista encadenada `LinkedList<Worker>`. Los asistentes (`Attender`), clase modelada para esta entrega los almacenará en una tabla de dispersión `HashTable<Attender>`. También implementa  una lista encadenada de valoraciones del evento `LinkedList<Rating> ratings` definido en la PEC1. También es preciso destacar que implementa la clase `Comparable`, que permitirá clasificar los eventos deportivos en función de sus ratings o del Id del evento (dependiendo de la estructura) en las estructuras que almacenan eventos deportivos, que se analizarán a continuación en la clase principal de la aplicación `SportEvents4ClubImpl`.
* Enrollment: La clase `Enrollment` que abstrae la inscripción de jugadores implementará la interfaz `Comparable`que permite ordenar los sustitutos en función del nivel.

### **Sports4ClubImpl**

#### En cuanto a las **estructuras de datos:** de la clase SportEvents4ClubImpl

Algunos de los cambios respecto a la PR1:

* Para almacenar a los jugadores se utilizará una tabla de dispersión `HashTable <Player>` según lo especificado en la PEC2.
* Los ficheros pasarán a implementarse utilizando una cola con prioridad 'PriorityQueue<File>' según lo definido en la PEC2.
* Para las entidades organizadoras se utilizará una tabla de dispersión `HashTable<OrganizingEntity>` según lo definido en la PEC2.
* Para almacenar las organizaciones que más espectadores traen se utilizón un vector ordenado `OrderedVector<OrganizingEntity>` según lo detallado en la PEC2.
* Para almacenar los roles se utilizó un vector de Java `Role[]` según lo establecido en la PEC2.
* Para almacenar los trabajadores se utilizó una tabla de dispersión `HashTable <Worker>` según lo especificado en la PEC2.
* Los eventos deportivos ahora se implementarán utilizando un AVL  `DictionaryAVLImpl<SportEvent>`


### **LevelHelper**

Se implementó en el módulo `util` la clase `LevelHelper` que permite determinar el nivel de un jugador a partir de el número de valoraciones asociadas e incluirlo en alguna de las categorías preestablecidas:

Esto se implementó mediante el método `getLevel()`:

    public static SportEvents4Club.Level getLevel(int numRatings){}