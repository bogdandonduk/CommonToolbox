package bogdandonduk.commontoolboxlib.extensions

@JvmOverloads
fun CharSequence.indicesOfAllOccurrences(char: Char, startIndex: Int = 0, ignoreCase: Boolean = true) = mutableListOf<Int>().apply {
    var stopIndex = startIndex

    while(stopIndex != -1 && stopIndex != length) {
        stopIndex = indexOf(char, stopIndex, ignoreCase)

        if(stopIndex != -1) {
            add(stopIndex)

            stopIndex ++
        }
    }
}

@JvmOverloads
fun CharSequence.indicesOfAllOccurrences(value: String, startIndex: Int = 0, ignoreCase: Boolean = true) = mutableListOf<Int>().apply {
    var stopIndex = startIndex

    while(stopIndex != -1 && stopIndex != length) {
        stopIndex = indexOf(value, stopIndex, ignoreCase)

        if(stopIndex != -1) {
            add(stopIndex)

            stopIndex ++
        }
    }
}