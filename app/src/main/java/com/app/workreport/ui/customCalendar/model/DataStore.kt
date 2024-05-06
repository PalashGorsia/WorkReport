package com.app.workreport.ui.customCalendar.model

class DataStore<V>(private val create: (offset: Int) -> V) : HashMap<Int, V>() {
    override fun get(key: Int): V {
        val value = super.get(key)
        return if (value == null) {
            val data = create(key)
            put(key, data)
            data
        } else {
            value
        }
    }
}