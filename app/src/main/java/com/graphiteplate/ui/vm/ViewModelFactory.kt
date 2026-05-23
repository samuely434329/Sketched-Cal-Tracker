package com.graphiteplate.ui.vm

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.graphiteplate.GraphitePlateApp
import com.graphiteplate.data.CalorieRepository

/**
 * Resolves the singleton [CalorieRepository] from the [GraphitePlateApp]
 * and supplies it to every ViewModel constructor. Compose code calls
 * [graphiteViewModel] with the desired type and gets a wired instance.
 *
 * The `inline reified` glue is kept tiny on purpose: the factory itself
 * is declared with `noinline` so the caller-supplied `build` lambda is a
 * regular runtime value, which avoids the "lambda type clash" you would
 * hit when forwarding it through a `crossinline` parameter.
 */
@Composable
inline fun <reified VM : ViewModel> graphiteViewModel(
    noinline build: (CalorieRepository) -> VM,
): VM {
    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                as GraphitePlateApp
            build(app.repository)
        }
    }
    return viewModel(factory = factory)
}
