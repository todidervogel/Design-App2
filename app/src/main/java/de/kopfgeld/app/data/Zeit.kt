package de.kopfgeld.app.data

/**
 * Dauer in Worten, wie sie in den Wireframes steht: "1 Std. 25 Min".
 */
fun formatiereDauer(minuten: Int): String {
    if (minuten < 60) return "$minuten Min"
    val stunden = minuten / 60
    val rest = minuten % 60
    return if (rest == 0) "$stunden Std." else "$stunden Std. $rest Min"
}

/** Sekunden als mm:ss, z. B. "12:40". */
fun formatiereUhr(sekunden: Int): String {
    val sicher = sekunden.coerceAtLeast(0)
    val m = sicher / 60
    val s = sicher % 60
    return "%02d:%02d".format(m, s)
}
