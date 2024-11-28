package com.example.databaselokal

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.databaselokal.databinding.ActivityAddHomeworkBinding
import com.example.localdatabase_057_082.Homework
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddHomeworkActivity : AppCompatActivity() {

    private var isEdit = false
    private var homework: Homework? = null
    private var position: Int = 0
    private lateinit var homeworkHelper: HomeworkHelper
    private lateinit var binding: ActivityAddHomeworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHomeworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeworkHelper = HomeworkHelper.getInstance(applicationContext)
        homeworkHelper.open()

        // Cek apakah ada data homework
        homework = intent.getParcelableExtra(EXTRA_HOMEWORK)
        if (homework != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            homework = Homework()
        }

        // Menentukan title action bar dan button berdasarkan kondisi
        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            actionBarTitle = "Ubah"
            btnTitle = "Update"
            homework?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }
        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnSubmit.text = btnTitle

        // Set listener untuk tombol
        binding.btnSubmit.setOnClickListener { addNewHomework() }
        binding.btnDelete.setOnClickListener { showAlertDialog(ALERT_DIALOG_DELETE) }
    }

    private fun addNewHomework() {
        val title = binding.edtTitle.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.edtTitle.error = "Title tidak boleh kosong"
            return
        }

        homework?.apply {
            this.title = title
            this.description = description
        }

        val intent = Intent().apply {
            putExtra(EXTRA_HOMEWORK, homework)
            putExtra(EXTRA_POSITION, position)
        }

        val values = ContentValues().apply {
            put(DatabaseContract.HomeworkColumns.TITLE, title)
            put(DatabaseContract.HomeworkColumns.DESCRIPTION, description)
        }

        if (isEdit) {
            val result = homeworkHelper.update(homework?.id.toString(), values)
            if (result > 0) {
                setResult(RESULT_UPDATE, intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show()
            }
        } else {
            homework?.date = getCurrentDate()
            values.put(DatabaseContract.HomeworkColumns.DATE, homework?.date)

            val result = homeworkHelper.insert(values)
            if (result > 0) {
                homework?.id = result.toInt()
                setResult(RESULT_ADD, intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal menambah data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan memperbaharui data?"
        } else {
            dialogTitle = "Hapus Homework"
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
        }

        val alertDialogBuilder = AlertDialog.Builder(this).apply {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton("Ya") { _, _ ->
                if (isDialogClose) {
                    finish()
                } else {
                    val result = homeworkHelper.deleteById(homework?.id.toString()).toLong()
                    if (result > 0) {
                        val intent = Intent().apply {
                            putExtra(EXTRA_POSITION, position)
                        }
                        setResult(RESULT_DELETE, intent)
                        finish()
                    } else {
                        Toast.makeText(this@AddHomeworkActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        }

        alertDialogBuilder.create().show()
    }

    companion object {
        const val EXTRA_HOMEWORK = "extra_homework"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_ADD = 101
        const val RESULT_UPDATE = 102
        const val RESULT_DELETE = 103
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
}
