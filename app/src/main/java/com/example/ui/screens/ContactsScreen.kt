package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CampusContact
import com.example.data.model.ContactCategory
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: CampusViewModel
) {
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf<ContactCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<CampusContact?>(null) }

    val filteredContacts = if (selectedCategory == null) {
        contacts
    } else {
        contacts.filter { it.category == selectedCategory }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_contact_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("contacts_screen")
        ) {
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All (${contacts.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ContactCategory.EMERGENCY,
                        onClick = { selectedCategory = ContactCategory.EMERGENCY },
                        label = { Text("🚨 Emergency") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ContactCategory.ADMINISTRATION,
                        onClick = { selectedCategory = ContactCategory.ADMINISTRATION },
                        label = { Text("🏛️ Administration") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ContactCategory.ACADEMIC,
                        onClick = { selectedCategory = ContactCategory.ACADEMIC },
                        label = { Text("🎓 Academic") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ContactCategory.SUPPORT,
                        onClick = { selectedCategory = ContactCategory.SUPPORT },
                        label = { Text("🛠️ Support") }
                    )
                }
            }

            if (filteredContacts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Emergency,
                    title = "No Contacts Found",
                    description = "Add important university numbers, security helplines, or department contacts for quick one-tap access.",
                    actionText = "Add Contact",
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Emergency Banner
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Campus Emergency Hotline",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Security & Medical services are available 24/7 campus-wide.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    items(filteredContacts, key = { it.id }) { contact ->
                        ContactCard(
                            contact = contact,
                            onDial = { dialNumber(context, contact.phone) },
                            onEmail = { sendEmail(context, contact.email) },
                            onDelete = { contactToDelete = contact }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, dept, role, phone, email, location, category, isEmergency ->
                viewModel.addContact(name, dept, role, phone, email, location, category, isEmergency)
                showAddDialog = false
            }
        )
    }

    if (contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            title = { Text("Delete Contact?") },
            text = { Text("Remove '${contactToDelete?.name}' from university directory?") },
            confirmButton = {
                Button(
                    onClick = {
                        contactToDelete?.let { viewModel.deleteContact(it) }
                        contactToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ContactCard(
    contact: CampusContact,
    onDial: () -> Unit,
    onEmail: () -> Unit,
    onDelete: () -> Unit
) {
    val isEmergency = contact.isEmergency

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("contact_card_${contact.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp).let {
            if (isEmergency) {
                CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                )
            } else {
                CardDefaults.elevatedCardColors()
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isEmergency) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (contact.category) {
                                ContactCategory.EMERGENCY -> Icons.Default.Emergency
                                ContactCategory.ADMINISTRATION -> Icons.Default.Shield
                                ContactCategory.ACADEMIC -> Icons.Default.LocalHospital
                                ContactCategory.SUPPORT -> Icons.Default.Phone
                            },
                            contentDescription = null,
                            tint = if (isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${contact.role} • ${contact.department}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            if (contact.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = contact.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quick Call & Email Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDial,
                    modifier = Modifier.weight(1f),
                    colors = if (isEmergency) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call: ${contact.phone}")
                }

                if (contact.email.isNotBlank()) {
                    OutlinedButton(
                        onClick = onEmail,
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Email")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, ContactCategory, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ContactCategory.ADMINISTRATION) }
    var isEmergency by remember { mutableStateOf(false) }

    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add University Contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name / Office Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role / Purpose") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Campus Location / Office") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        ContactCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark as 24/7 Emergency Helpline")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, department, role, phone, email, location, category, isEmergency)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun dialNumber(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone.replace(" ", "")}")
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun sendEmail(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
