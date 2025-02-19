// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.Status
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContact(application: ContactsApplication, context: Context, contactsViewModel: ContactsViewModel = viewModel(factory = ContactsViewModelFactory(application))) {
    val contacts: List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    val selected: Contact? by contactsViewModel.selectedContact.observeAsState(null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        contactsViewModel.setup()
                    }) { Icon(painter = painterResource(R.drawable.populate), contentDescription = null) }
                    IconButton(onClick = {
                        contactsViewModel.sync()
                    }) { Icon(painter = painterResource(R.drawable.synchronize), contentDescription = null) }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newContact = Contact(name = "", status = Status.NEW)
                contactsViewModel.selectContact(newContact)
                Toast.makeText(context, context.getString(R.string.new_contact), Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            if (selected != null) {
                EditContactScreen(
                    contactsViewModel = contactsViewModel,
                    onSave = { contact ->
                        if (contact.status != Status.NEW) {
                            contact.status = Status.MODIFIED
                        }
                        Toast.makeText(context,
                            context.getString(R.string.contact_save), Toast.LENGTH_SHORT).show()
                        contactsViewModel.save(contact)
                        contactsViewModel.selectContact(null)
                    },
                    onDelete = {
                        if (selected?.id != null) {
                            contactsViewModel.delete(selected?.id!!)
                            Toast.makeText(context,
                                context.getString(R.string.contact_deletion), Toast.LENGTH_SHORT).show()
                            contactsViewModel.selectContact(null)
                        }
                    },
                    onCancel = {
                        contactsViewModel.selectContact(null)
                    }
                )
            } else {
                ScreenContactList(contacts, onContactSelected = { selectedContact ->
                    contactsViewModel.selectContact(selectedContact)
                })
            }
        }
    }
}