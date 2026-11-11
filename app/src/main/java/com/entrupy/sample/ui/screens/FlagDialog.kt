package com.entrupy.sample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.entrupy.sample.ui.theme.AccentGold
import com.entrupy.sample.ui.theme.CardBackground
import com.entrupy.sample.ui.theme.TextSecondary
import com.entrupy.sdk.app.EntrupyApp
import com.entrupy.sdk.listeners.FlagDetailsCallback
import com.entrupy.sdk.listeners.SetFlagCallback
import com.entrupy.sdk.model.EntrupyFlagDetails
import com.entrupy.sdk.model.EntrupyFlagReason

/**
 * A custom flag UI built on the SDK's data APIs: `getFlagDetails` to know whether the item can be
 * flagged and with which reasons, then `setFlag` to flag it.
 *
 * @param onFlagChanged Called after the flag was set, so the list can show the change.
 */
@Composable
fun FlagDialog(
    entrupyApp: EntrupyApp,
    entrupyId: String,
    onDismiss: () -> Unit,
    onFlagChanged: () -> Unit,
) {
    var flagDetails by remember { mutableStateOf<EntrupyFlagDetails?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf<EntrupyFlagReason?>(null) }
    var message by remember { mutableStateOf("") }
    // Callbacks run on the main thread, but can arrive after the dialog closed.
    var isOpen by remember { mutableStateOf(true) }

    DisposableEffect(entrupyId) {
        entrupyApp.getFlagDetails(
            entrupyId = entrupyId,
            callback =
                object : FlagDetailsCallback {
                    override fun onFlagDetailsFetched(details: EntrupyFlagDetails) {
                        if (isOpen) flagDetails = details
                    }

                    override fun onFlagDetailsError(errorCode: Int, description: String) {
                        if (isOpen) error = "$description ($errorCode)"
                    }
                },
        )
        onDispose { isOpen = false }
    }

    val setFlagCallback =
        object : SetFlagCallback {
            override fun onFlagSet(entrupyId: String, flag: Boolean) {
                if (!isOpen) return
                onFlagChanged()
                onDismiss()
            }

            override fun onSetFlagError(errorCode: Int, description: String) {
                if (!isOpen) return
                isSending = false
                error = "$description ($errorCode)"
            }
        }

    val current = flagDetails
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Flag $entrupyId") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when {
                    current == null && error == null ->
                        CircularProgressIndicator(color = AccentGold, modifier = Modifier.size(24.dp))
                    current == null -> Unit
                    current.canFlag ->
                        ReasonPicker(
                            reasons = current.supportedFlagReasons,
                            selected = selectedReason,
                            onSelect = { selectedReason = it },
                            message = message,
                            onMessageChange = { message = it },
                        )
                    current.flagStatus.isFlagged -> {
                        val info = current.flagStatus.flagInfo
                        Text("This item is flagged.")
                        info?.label?.let { Text("Reason: $it", color = TextSecondary) }
                        info?.message?.takeIf { it.isNotBlank() }?.let {
                            Text("Note: $it", color = TextSecondary)
                        }
                    }
                    else -> Text("This item cannot be flagged.")
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            when {
                current?.canFlag == true -> {
                    val reason = selectedReason
                    val canSend =
                        reason != null && (!reason.isMessageRequired || message.isNotBlank())
                    TextButton(
                        enabled = canSend && !isSending,
                        onClick = {
                            isSending = true
                            error = null
                            entrupyApp.setFlag(
                                flag = true,
                                entrupyId = entrupyId,
                                reasonId = reason?.id,
                                message = message,
                                callback = setFlagCallback,
                            )
                        },
                    ) {
                        Text("Flag", color = AccentGold)
                    }
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close", color = TextSecondary) } },
        containerColor = CardBackground,
    )
}

@Composable
private fun ReasonPicker(
    reasons: List<EntrupyFlagReason>,
    selected: EntrupyFlagReason?,
    onSelect: (EntrupyFlagReason) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
) {
    if (reasons.isEmpty()) {
        Text("No flag reasons are set up for this item.", color = TextSecondary)
        return
    }
    Text("Why are you flagging this result?")
    reasons.forEach { reason ->
        Row(
            modifier =
                Modifier.fillMaxWidth().selectable(
                    selected = reason.id == selected?.id,
                    onClick = { onSelect(reason) },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = reason.id == selected?.id,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = AccentGold),
            )
            Text(reason.label)
        }
    }
    val isRequired = selected?.isMessageRequired == true
    OutlinedTextField(
        value = message,
        onValueChange = onMessageChange,
        label = { Text(if (isRequired) "Note (required)" else "Note (optional)") },
        modifier = Modifier.fillMaxWidth(),
    )
}
