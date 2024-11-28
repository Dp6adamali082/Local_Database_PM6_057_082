import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Nama tabel dan kolom-kolomnya
    private val TABLE_NAME = "notes"
    private val COLUMN_ID = "id"
    private val COLUMN_TITLE = "title"
    private val COLUMN_DESCRIPTION = "description"
    private val COLUMN_DATE = "date"

    // SQL untuk membuat tabel
    private val CREATE_TABLE_NOTE = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_DATE TEXT
        )
    """

    // Dipanggil saat pertama kali database dibuat
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_NOTE)
    }

    // Dipanggil saat versi database diupgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Menghapus tabel lama jika ada
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // Membuat ulang tabel
        onCreate(db)
    }

    companion object {
        // Nama database dan versi
        private const val DATABASE_NAME = "dbhomework"
        private const val DATABASE_VERSION = 1
    }
}
