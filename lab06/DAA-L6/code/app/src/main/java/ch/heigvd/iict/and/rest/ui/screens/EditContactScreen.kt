// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.Status
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel

// Stateful edit contact form with state based on ContactsViewModel.selectedContact
@Composable
fun EditContactScreen(
    contactsViewModel: ContactsViewModel = viewModel(),
    onSave: (Contact) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    // Custom saver to support rememberSaveable
    val contactSaver: Saver<Contact, Any> = listSaver(
        save = { listOf(it.id, it.remote_id, it.status, it.name, it.firstname, it.birthday, it.email, it.address, it.zip, it.city, it.type, it.phoneNumber) },
        restore = {
            Contact(
                id = it[0] as Long,
                remote_id = it[1] as Long?,
                status = it[2] as Status,
                name = it[3] as String,
                firstname = it[4] as String?,
                birthday = it[5] as String?,
                email = it[6] as String?,
                address = it[7] as String?,
                zip = it[8] as String?,
                city = it[9] as String?,
                type = it[10] as PhoneType,
                phoneNumber = it[11] as String?
            )
        }
    )

    // rememberSaveable is important to support rotating screen during edition
    var contact by rememberSaveable(stateSaver = contactSaver) { mutableStateOf(contactsViewModel.selectedContact.value!!) }

    EditContact(
        contact = contact,
        onNameChange = { contact = contact.copy(name = it) },
        onFirstnameChange = { contact = contact.copy(firstname = it) },
        onEmailChange = { contact = contact.copy(email = it) },
        onAddressChange = { contact = contact.copy(address = it) },
        onZipChange = { contact = contact.copy(zip = it) },
        onCityChange = { contact = contact.copy(city = it) },
        onPhoneTypeChange = { contact = contact.copy(type = it) },
        onPhoneNumberChange = { contact = contact.copy(phoneNumber = it) },
        onSave = { onSave(contact) },
        onDelete = onDelete,
        onCancel = onCancel
    )
}

// Stateful edit contact form
@Composable
fun EditContact(
    contact: Contact,
    onNameChange: (String) -> Unit,
    onFirstnameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onZipChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onPhoneTypeChange: (PhoneType) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(state = ScrollState(0))) {
        EditorContent(
            value = contact.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.screen_edit_name)
        )
        EditorContent(
            value = contact.firstname ?: "",
            onValueChange = onFirstnameChange,
            label = stringResource(R.string.screen_edit_firstname)
        )
        EditorContent(
            value = contact.email ?: "",
            onValueChange = onEmailChange,
            label = stringResource(R.string.screen_edit_email)
        )
        EditorContent(
            value = contact.address ?: "",
            onValueChange = onAddressChange,
            label = stringResource(R.string.screen_edit_address)
        )
        EditorContent(
            value = contact.zip ?: "",
            onValueChange = onZipChange,
            label = stringResource(R.string.screen_edit_zip)
        )
        EditorContent(
            value = contact.city ?: "",
            onValueChange = onCityChange,
            label = stringResource(R.string.screen_edit_city)
        )
        PhoneType(
            selectedOption = contact.type ?: PhoneType.MOBILE,
            onOptionSelected = onPhoneTypeChange
        )
        TextField(
            value = contact.phoneNumber ?: "",
            onValueChange = onPhoneNumberChange,
            label = { Text(stringResource(R.string.screen_edit_phone)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.screen_edit_save))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.screen_edit_delete))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.screen_edit_cancel))
        }
    }
}

// Simple abstraction to avoid repeating TextField and Spacer
@Composable
fun EditorContent(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        label = { Text(label) },
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

// Phone type radio buttons selector
@Composable
fun PhoneType(selectedOption: PhoneType, onOptionSelected: (PhoneType) -> Unit) {
    val radioOptions = PhoneType.entries

    Column {
        Text(stringResource(R.string.phone_type))
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(stringResource(text.stringRes()))
                }
            }
        }
    }
}
