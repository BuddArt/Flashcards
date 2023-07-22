package flashcards

import java.io.File
import kotlin.system.exitProcess

val mapOfCards = mutableMapOf<String, String>()
val listOfMistakes = mutableListOf<String>()
var mistakesTerm = mutableListOf<String>()
var countOfTerm = 0
var logText: String = ""

fun main(args: Array<String>) {
    for (n in args) {
        if (n == "-import") import(args[args.indexOf(n) + 1])
    }
    while (true) {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readln()) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import()
            "export" -> export()
            "ask" -> ask()
            "hardest card" -> hardestCard()
            "log" -> log()
            "reset stats" -> resetStats()
            "exit" -> {
                for (n in args) {
                    if (n == "-export") export(args[args.indexOf(n) + 1])
                }
                println("Bye Bye!")
                exitProcess(1)
            }
        }
        println()
    }
}

fun add() {
    println("The card:")
    val term = readln()
    if (mapOfCards.containsKey(term)) {
        println("The card \"$term\" already exists.")
        return
    }
    println("The definition of the card:")
    val definition = readln()
    if (mapOfCards.containsValue(definition)) {
        println("The definition \"$definition\" already exists.")
        return
    }
    mapOfCards[term] = definition
    println("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove() {
    println("Which card?")
    val termToRemove = readln()
    if (mapOfCards.containsKey(termToRemove)) {
        mapOfCards.remove(termToRemove)
        println("The card has been removed.")
    } else println("Can't remove \"$termToRemove\": there is no such card.")
}

fun import(name: String = "") {
    val nameFile: String = if (name == "") {
        println("File name:")
        readln()
    } else name
    val file = File(nameFile)
    if (!file.exists()) {
        println("File not found.")
        return
    } else {
        val (cards, mistakes, count) = file.readText().split(" | ")
        val listMistakes = mistakes.removePrefix("[").removeSuffix("]").split(", ")
        for (i in listMistakes.indices) {
            repeat(count.toInt()) {
                listOfMistakes.add(listMistakes[i])
            }
        }
        countOfTerm = count.toInt()
        val importFile = cards.removePrefix("{").removeSuffix("}")
        val importMap = mutableMapOf<String, String>()
        val importCards = importFile.split(", ")
        for (card in importCards) {
            val (importTerm, importDefinition) = card.split("=")
            importMap[importTerm] = importDefinition
        }
        println("${importMap.size} cards have been loaded.")
        for (card in importMap) {
            if (mapOfCards.containsKey(card.key)) {
                mapOfCards[card.key] = card.value
            } else mapOfCards[card.key] = card.value
        }
    }
}

fun export(name: String = "") {
    val nameFile: String = if (name == "") {
        println("File name:")
        readln()
    } else name
    if (File(nameFile).exists()) {
        File(nameFile).delete()
    }
    val file = File(nameFile)
    file.writeText(mapOfCards.toString() + " | " + mistakesTerm.joinToString(", ") + " | " + countOfTerm.toString())
    println("${mapOfCards.size} cards have been saved.")
}

fun ask() {
    println("How many times to ask?")
    val timesToAsk = readln().toInt()
    repeat(timesToAsk) {
        val cards = mapOfCards.entries.random()
        println("Print the definition of \"${cards.key}\":")
        val answer = readln()
        if (answer == cards.value) {
            println("Correct!")
        } else if (mapOfCards.containsValue(answer) && answer != cards.value) {
            println("Wrong. The right answer is \"${cards.value}\", but your definition is correct for \"${mapOfCards.keys.first { answer == mapOfCards[it] }}\".")
            listOfMistakes.add(cards.key)
        } else {
            println("Wrong. The right answer is \"${cards.value}\".")
            listOfMistakes.add(cards.key)
        }
    }
}

fun hardestCard() {
    for (cards in mapOfCards) {
        val count = listOfMistakes.count{it == cards.key}
        if (count > countOfTerm) {
            countOfTerm = count
            mistakesTerm.clear()
            mistakesTerm.add(cards.key)
        } else if (count == countOfTerm && countOfTerm >= 1) {
            mistakesTerm.add(cards.key)
        }
    }
    val strOne = "The hardest card is "
    var strMany = "The hardest cards are "
    if (countOfTerm == 0) println("There are no cards with errors.")
    else if (mistakesTerm.size == 1) {
        println(strOne + "\"${mistakesTerm[0]}\"." + "You have $countOfTerm errors answering it.")
    } else {
        for (i in mistakesTerm.indices) {
            strMany += if (i == mistakesTerm.lastIndex) "\"${mistakesTerm[i]}\"." else "\"${mistakesTerm[i]}\", "
        }
        println(strMany + "You have $countOfTerm errors answering them.")
    }
}

fun println(str: String = "") {
    kotlin.io.println(str)
    logText += str + "\n"
}

fun readln(): String {
    val rl = kotlin.io.readln()
    logText += rl + "\n"
    return rl
}

fun log() {
    println("File name:")
    val nameFile = readln()
    File(nameFile).writeText(logText)
    println("The log has been saved.")
}

fun resetStats() {
    listOfMistakes.clear()
    mistakesTerm.clear()
    countOfTerm = 0
    println("Card statistics have been reset.")
}