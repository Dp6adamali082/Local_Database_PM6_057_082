package com.example.localdatabase

import android.database.Cursor
import android.provider.BaseColumns
import com.example.localdatabase_057_082.DatabaseContract
import com.example.localdatabase_057_082.Homework

object MappingHelper {

    fun mapCursorToArrayList(homeworkCursor: Cursor?): ArrayList<Homework> {
        val homeworkList = ArrayList<Homework>()

        homeworkCursor?.apply {
            while (moveToNext()) {
                // Mengambil data kolom menggunakan getColumnIndexOrThrow
                val id = getInt(getColumnIndexOrThrow(BaseColumns._ID))  // Menggunakan BaseColumns._ID
                val title = getString(getColumnIndexOrThrow(DatabaseContract.HomeworkColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.HomeworkColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.HomeworkColumns.DATE))

                // Menambahkan objek Homework ke dalam list
                homeworkList.add(Homework(id, title, description, date))
            }
        }

        return homeworkList
    }
}