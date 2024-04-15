package com.mehmetalan.notebook

import android.app.Application
import com.mehmetalan.notebook.data.AppContainer
import com.mehmetalan.notebook.data.AppDataContainer
import com.mehmetalan.notebook.history.HistoryDatabase

class NoteApplication: Application() {
    lateinit var historyDatabase: HistoryDatabase
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        historyDatabase = HistoryDatabase.getDatabase(this)
    }
}