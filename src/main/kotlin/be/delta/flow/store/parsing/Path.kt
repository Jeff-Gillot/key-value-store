package be.delta.flow.store.parsing

data class Path(
    val items: List<String>
) {
    operator fun plus(name: String): Path {
        return this + parse(name)
    }

    operator fun plus(path: Path): Path {
        return Path(items + path.items)
    }

    override fun toString(): String {
        return items.fold("") { str, item ->
            buildString {
                append(str)
                if (str.isNotEmpty() && !item.startsWith("[")) {
                    append(".")
                }
                append(item)
            }
        }
    }

    companion object {
        val empty = Path(emptyList())

        fun parse(path: String): Path {
            val parts = mutableListOf<String>()
            val currentPart = StringBuilder()

            path.forEach { char ->
                when {
                    char == '.' && currentPart.firstOrNull() != '[' -> {
                        if (currentPart.isNotEmpty()) parts.add(currentPart.toString())
                        currentPart.clear()
                    }

                    char == '[' -> {
                        if (currentPart.isNotEmpty()) parts.add(currentPart.toString())
                        currentPart.clear()
                        currentPart.append(char)
                    }

                    char == ']' -> {
                        currentPart.append(char)
                        if (currentPart.isNotEmpty()) parts.add(currentPart.toString())
                        currentPart.clear()
                    }

                    else -> currentPart.append(char)
                }
            }
            if (currentPart.isNotEmpty()) parts.add(currentPart.toString())
            return Path(parts)
        }
    }
}