package com.graphiteplate

import android.app.Application
import com.graphiteplate.data.CalorieRepository
import com.graphiteplate.data.local.AppDatabase
import com.graphiteplate.data.prefs.UserPreferences

/**
 * Manual dependency container. With only one repository and a couple of
 * stores, a Hilt setup would be more boilerplate than payoff; lazy-init
 * here is enough to keep ViewModels testable via constructor injection.
 */
class GraphitePlateApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }
    val repository: CalorieRepository by lazy {
        CalorieRepository(database, userPreferences)
    }
}
