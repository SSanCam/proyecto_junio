package utilities

fun showHelp(console: Console) {
    console.showInfo("""
        Uso del programa:
        -g <grupoDesc>                  Añade un nuevo grupo con la descripción <grupoDesc>.
        -p <ctfId> <grupoId> <puntuacion> Añade una participación del grupo <grupoId> en el CTF <ctfId> con la puntuación <puntuacion>.
        -t <grupoId>                    Elimina el grupo <grupoId> y todas sus participaciones en los CTF.
        -e <ctfId> <grupoId>            Elimina la participación del grupo <grupoId> en el CTF <ctfId>.
        -l [<grupoId>]                  Muestra la información del grupo <grupoId> y sus participaciones. Si <grupoId> no está presente, muestra la información de todos los grupos.
        -c                              Muestra la participación de los grupos en todos los CTF, ordenado de mayor a menor puntuación.
        -f <filepath>                   Procesa el archivo <filepath> con un conjunto de comandos para procesamiento por lotes.
        -i                              Lanza la interfaz gráfica.
        -h, --help                      Muestra este mensaje de ayuda.
    """.trimIndent())
}