package com.prosecshane.zametki.notes

import com.google.gson.Gson
import java.util.*

fun noteFromJsonWithType(json: String?): Note {
    if (json == null) { return Note() }
    return when (json.slice(
        json.indexOf("\"typeOfNote\":\"") + 14
                ..json.indexOf("\"typeOfNote\":\"") + 14
    )) {
        "T" -> Note(json = json)
        "C" -> CheckNote(json = json)
        "A" -> AlarmNote(json = json)
        "I" -> ImageNote(json = json)
        else -> throw Exception("noteFromJsonWithType: Exception occurred")
    }
}

open class Note(var title: String = "Новая Заметка", open var content: String = "") {
    var typeOfNote: String = "Text"
    internal var id = this.generateRandomId()
    internal var lastUsed = System.currentTimeMillis()

    constructor(json: String) : this("", "") {
        this.fromJson(json)
    }

    fun getLastUsed(): Long { return this.lastUsed }
    fun updateLastUsed() { this.lastUsed = System.currentTimeMillis() }

    fun getNoteTitle(): String { return this.title }
    fun setNoteTitle(newTitle: String) { this.title = newTitle }

    open fun getNoteContent(): String { return this.content }
    open fun setNoteContent(newContent: String) { this.content = newContent }

    fun getId(): Long { return this.id }
    private fun generateRandomId(): Long { return (0..Long.MAX_VALUE).random() }
    fun assignNewRandomId() { this.id = this.generateRandomId() }
    fun generateExclusiveId(occupiedIds: MutableList<Long>) {
        while (this.id in occupiedIds) { this.assignNewRandomId() }
    }

    fun toJson(): String { return Gson().toJson(this) }
    open fun fromJson(value: String) {
        val asNote = Gson().fromJson(value, Note::class.java)
        if (this.typeOfNote == asNote.typeOfNote) {
            this.title = asNote.title
            this.content = asNote.content
            this.id = asNote.id
            this.lastUsed = asNote.lastUsed
        }
    }
}

const val CNContentDepr = ".content is not used for CheckNote, use .unchecked and .checked instead"
class CheckNote(title: String = "Новая Заметка", content: String = "") : Note(title, content) {
    init { this.typeOfNote = "Check" }

    constructor(json: String) : this("", "") {
        this.fromJson(json)
    }

    @Deprecated(CNContentDepr) override var content: String
        @Deprecated(CNContentDepr) get() = super.content
        @Deprecated(CNContentDepr) set(value) {}

    var unchecked = mutableListOf("")
    private var checked = MutableList<String>(0){""}

    fun changeUncheckedLine(i: Int, value: String) { this.unchecked[i] = value }
    fun appendUnchecked(value: String) { this.unchecked.add(value) }
    private fun swapWithNearby(i: Int, range: Int) {
        val temp = this.unchecked[i]
        this.unchecked[i] = this.unchecked[i+range]
        this.unchecked[i+range] = temp
    }
    fun swapWithHigher(i: Int) { this.swapWithNearby(i, -1)}
    fun swapWithLower(i: Int) { this.swapWithNearby(i, 1)}
    fun checkUnchecked(i: Int) {
        this.appendChecked(this.unchecked[i])
        this.deleteUnchecked(i)
    }
    fun deleteUnchecked(i: Int) { this.unchecked.removeAt(i) }

    fun getChecked(): MutableList<String> { return this.checked }
    fun appendChecked(value: String) { this.checked.add(value) }
    fun uncheckChecked(i: Int) {
        this.appendUnchecked(this.checked[i])
        this.deleteChecked(i)
    }
    fun deleteChecked(i: Int) { this.checked.removeAt(i) }

    override fun fromJson(value: String) {
        val asNote = Gson().fromJson(value, CheckNote::class.java)
        if (this.typeOfNote == asNote.typeOfNote) {
            this.title = asNote.title
            this.checked = asNote.checked
            this.unchecked = asNote.unchecked
            this.id = asNote.id
            this.lastUsed = asNote.lastUsed
        }
    }
}

class AlarmNote(title: String = "Новая Заметка", content: String = "") : Note(title, content) {
    init { this.typeOfNote = "Alarm" }
    var alarmTime: Calendar = Calendar.getInstance()

    init {
        this.setAlarmTime(
            this.alarmTime.get(Calendar.YEAR),
            this.alarmTime.get(Calendar.MONTH),
            this.alarmTime.get(Calendar.DAY_OF_MONTH) + 1,
            this.alarmTime.get(Calendar.HOUR_OF_DAY),
            0
        )
    }

    constructor(json: String) : this("", "") {
        this.fromJson(json)
    }

    fun getAlarmTimeAsString(): String {
        val hour = this.alarmTime.get(Calendar.HOUR_OF_DAY)
        val minute = this.alarmTime.get(Calendar.MINUTE)
        val day = this.alarmTime.get(Calendar.DAY_OF_MONTH)
        val month = when (this.alarmTime.get(Calendar.MONTH)) {
            0 -> "янв."
            1 -> "фев."
            2 -> "мар."
            3 -> "апр."
            4 -> "мая"
            5 -> "июн."
            6 -> "июл."
            7 -> "авг."
            8 -> "сен."
            9 -> "окт."
            10 -> "ноя."
            11 -> "дек."
            else -> "###"
        }
        val year = this.alarmTime.get(Calendar.YEAR)
        return "${hour.toString().padStart(2, '0'
        )}:${minute.toString().padStart(2, '0')}, $day $month $year г."
    }

    fun setAlarmTime(year: Int, month: Int, day: Int,
                     hour: Int, minute: Int) {
        alarmTime.set(year, month, day, hour, minute, 0)
    }

    override fun fromJson(value: String) {
        val asNote = Gson().fromJson(value, AlarmNote::class.java)
        if (this.typeOfNote == asNote.typeOfNote) {
            this.title = asNote.title
            this.content = asNote.content
            this.alarmTime = asNote.alarmTime
            this.id = asNote.id
            this.lastUsed = asNote.lastUsed
        }
    }
}

class ImageNote(title: String = "Новая Заметка", content: String = "") : Note(title, content) {
    init { this.typeOfNote = "Image" }

    constructor(json: String) : this("", "") {
        this.fromJson(json)
    }

    override fun fromJson(value: String) {
        val asNote = Gson().fromJson(value, ImageNote::class.java)
        if (this.typeOfNote == asNote.typeOfNote) {
            this.title = asNote.title
            this.content = asNote.content
            this.id = asNote.id
            this.lastUsed = asNote.lastUsed
        }
    }
}
