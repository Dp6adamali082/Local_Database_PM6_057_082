package com.example.localdatabase_057_082

import android.provider.BaseColumns

internal class DatabaseContract {
    internal class HomeworkColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "homework"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"
        }
    }
}