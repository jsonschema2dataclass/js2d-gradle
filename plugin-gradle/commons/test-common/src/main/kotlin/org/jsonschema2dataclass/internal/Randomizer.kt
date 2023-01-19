package org.jsonschema2dataclass.internal

private val random = java.security.SecureRandom() // should we use .asKotlinRandom() here?
private val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/**
 * Return random value from list
 */
fun <T> randomValueFromList(values: List<T>): T = values[random.nextInt(values.size)]

/**
 * Return random value from an enum
 */
inline fun <reified T : Enum<T>> randomEnum(): T = randomValueFromList(enumValues<T>().asList())

/**
 * Return random alphanumeric list with length (1..11)
 */
fun randomString(len: Int = 10): String =
    List(random.nextInt(len) + 1) { randomValueFromList(alphaNumeric) }.joinToString("")

/**
 * Create a random list with random size (0..10) and values with random len (1..11)
 */
fun randomList(size: Int = 10, len: Int = 10) = List(random.nextInt(size)) { randomString(len) }

/**
 * Create a random set with random size (0..10) and values with random len (1..11)
 */
fun randomSet(size: Int = 10, len: Int = 10) = randomList(size, len).toSet()

/**
 * Create a random set with random size (0..10) and keys and values with random len (1..11)
 */
fun randomMap(size: Int = 10, len: Int = 10) =
    List(random.nextInt(size)) { randomString(len) to randomString(len) }.toMap()

/**
 * Return random boolean.
 */
fun randomBoolean() = random.nextBoolean()

/**
 * Convert value to null randomly
 */
fun <T> nullable(value: T): T? = if (randomBoolean()) null else value
