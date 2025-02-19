package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {
    private val TAG = this.javaClass.simpleName.toString()

    private val repository = application.repository

    val allContacts = repository.allContacts

    private var _selectedContact = MutableLiveData<Contact?>()
    val selectedContact : LiveData<Contact?> get() = _selectedContact

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    /**
     * Corresponds to the setup button -> trash db + enroll + loadAllContacts
     */
    fun setup() {
        Log.d(TAG, "Setup button")
        viewModelScope.launch {
            repository.trashDb()
            Log.d(TAG, "Enrolling...")
            repository.enroll()
            val contacts = repository.loadAllContacts()
            Log.d(TAG, "Contacts: $contacts")
        }
    }

    /**
     * General sync action reached from the sync button
     */
    fun sync() {
        viewModelScope.launch {
            repository.sync()
        }
    }

    /**
     * Save action via the Save button used to update a contact or create a new one if needed
     */
    fun save(contact: Contact) {
        viewModelScope.launch {
            repository.save(contact)
        }
    }

    /**
     * Delete action used via the Delete button
     */
    fun delete(id: Long) {
        viewModelScope.launch {
            repository.tryDeleteContact(id)
        }
    }
}

class ContactsViewModelFactory(private val application: ContactsApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}