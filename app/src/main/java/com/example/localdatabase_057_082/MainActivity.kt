package com.example.modul7

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localdatabase_057_082.Homework
import com.example.localdatabase_057_082.HomeworkAdapter
import com.example.modul7.databi.Actinding.vityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HomeworkAdapter

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null) {
            when (result.resultCode) {
                AddHomeworkActivity.RESULT_ADD -> {
                    val homework = result.data?.getParcelableExtra<Homework>(AddHomeworkActivity.EXTRA_HOMEWORK) as Homework
                    adapter.addItem(homework)
                    binding.rvHomework.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Data berhasil ditambahkan")
                }
                AddHomeworkActivity.RESULT_UPDATE -> {
                    val homework = result.data?.getParcelableExtra<Homework>(AddHomeworkActivity.EXTRA_HOMEWORK) as Homework
                    val position = result.data?.getIntExtra(AddHomeworkActivity.EXTRA_POSITION, 0) as Int
                    adapter.updateItem(position, homework)
                    binding.rvHomework.smoothScrollToPosition(position)
                    showSnackbarMessage("Data berhasil diubah")
                }
                AddHomeworkActivity.RESULT_DELETE -> {
                    val position = result.data?.getIntExtra(AddHomeworkActivity.EXTRA_POSITION, 0) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Data berhasil dihapus")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Homework"

        binding.rvHomework.layoutManager = LinearLayoutManager(this)
        binding.rvHomework.setHasFixedSize(true)

        adapter = HomeworkAdapter(object : HomeworkAdapter.OnItemClickCallback {
            override fun onItemClick(selectedHomework: Homework, position: Int) {
                val intent = Intent(this@MainActivity, AddHomeworkActivity::class.java)
                intent.putExtra(AddHomeworkActivity.EXTRA_HOMEWORK, selectedHomework)
                intent.putExtra(AddHomeworkActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
A
        binding.rvHomework.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddHomeworkActivity::class.java)
            resultLauncher.launch(intent)
        }

        if (savedInstanceState == null) {
            loadHomeworkAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Homework>(EXTRA_STATE)
            if (list != null) {
                adapter.listHomework = list
            }
        }
    }

    private fun loadHomeworkAsync() {
        lifecycleScope.launch {
            val homeworkHelper = HomeworkHelper.getInstance(applicationContext)
            homeworkHelper.open()
            val deferredHomework = async(Dispatchers.IO) {
                val cursor = homeworkHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val homework = deferredHomework.await()
            if (homework.isNotEmpty()) {
                adapter.listHomework = homework
            } else {
                adapter.listHomework = ArrayList()
                showSnackbarMessage("Data tidak ada")
            }
            homeworkHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listHomework)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvHomework, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}