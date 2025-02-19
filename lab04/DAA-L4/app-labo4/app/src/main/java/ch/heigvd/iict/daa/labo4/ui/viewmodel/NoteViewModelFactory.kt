// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.heigvd.iict.daa.labo4.data.repository.Repository

/**
 * Factory class for creating instances of NoteViewModel with a specific repository.
 *
 * @property repository The repository to be passed to the NoteViewModel.
 */
class NoteViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given ViewModel class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the ViewModel.
     * @throws IllegalArgumentException if the modelClass is not assignable from NoteViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}