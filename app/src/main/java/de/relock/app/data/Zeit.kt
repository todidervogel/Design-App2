package de.relock.app.data

/** Dauer in Worten, wie sie in den Wireframes steht: "1 Std. 5 Min". */
fun dauer(minuten: Int): String {
    if (minuten < 60) return "$minuten Min"
    val stunden = minuten / 60
    val rest = minuten % 60
    return if (rest == 0) "$stunden Std." else "$stunden Std. $rest Min"
}

/** Sekunden als mm:ss, z. B. "12:40". */
fun uhr(sekunden: Int): String {
    val sicher = sekunden.coerceAtLeast(0)
    return "%02d:%02d".format(sicher / 60, sicher % 60)
}

/** Zahl mit Tausenderpunkt, wie im Brain: "1.340". */
fun zahl(wert: Int): String {
    val text = wert.toString()
    if (text.length <= 3) return text
    return text.reversed().chunked(3).joinToString(".").reversed()
}
