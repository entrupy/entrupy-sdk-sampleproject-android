package com.entrupy.sample.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.entrupy.sample.history.AuthenticationHistory
import com.entrupy.sample.ui.theme.AccentGold
import com.entrupy.sample.ui.theme.Background
import com.entrupy.sample.ui.theme.Border
import com.entrupy.sample.ui.theme.TextSecondary
import com.entrupy.sdk.app.EntrupyApp
import com.entrupy.sdk.model.EntrupyCaptureResult
import com.entrupy.sdk.model.EntrupyFlagId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

private const val TAG = "EntrupySampleApp"

/**
 * The user's past authentications, like the Results tab of the iOS sample.
 *
 * Tap a row to open its result in the SDK. A row also offers Retake when the item has an open
 * retake request, and Market Edge when the item supports it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(modifier: Modifier = Modifier) {
    val entrupyApp = remember { EntrupyApp.sharedInstance() }
    val history = remember { AuthenticationHistory(entrupyApp) }
    val isAuthorized = remember { entrupyApp.isAuthorizationValid() }

    // Reload each time the tab opens, so a capture or a retake done meanwhile shows up.
    LaunchedEffect(isAuthorized) { if (isAuthorized) history.reload() }

    Column(modifier = modifier.fillMaxSize().background(Background)) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
        )

        if (!isAuthorized) {
            Message("Log in on the Home tab to see your results.")
            return
        }

        history.totalCount?.let { total ->
            Text(
                text = "$total authentications in the last 30 days",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }

        PullToRefreshBox(
            isRefreshing = history.isRefreshing,
            onRefresh = history::reload,
            modifier = Modifier.fillMaxSize(),
        ) {
            val errorMessage = history.errorMessage
            when {
                history.items.isEmpty() && history.isLastPage ->
                    PullableMessage { Message("No authentications found.") }
                history.items.isEmpty() && errorMessage != null ->
                    PullableMessage { ErrorMessage(errorMessage, onRetry = history::reload) }
                else -> HistoryList(entrupyApp, history)
            }
        }
    }
}

@Composable
private fun HistoryList(entrupyApp: EntrupyApp, history: AuthenticationHistory) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        listState.nextPageWanted(history).collect { history.loadNextPage() }
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        itemsIndexed(history.items, key = { _, item -> item.entrupyId }) { index, item ->
            AuthenticationRow(
                item = item,
                onOpen = { entrupyApp.openSafely("detail") { displayDetailViewForItem(item.entrupyId) } },
                onRetake = { entrupyApp.openSafely("retake") { startRetakeCapture(item.entrupyId) } },
                onMarketEdge = {
                    entrupyApp.openSafely("Market Edge") { displayMarketEdgeView(item.entrupyId) }
                },
            )
            if (index < history.items.lastIndex) HorizontalDivider(color = Border)
        }
        // While refreshing, the pull indicator is the only spinner.
        if (!history.isLastPage && !history.isRefreshing) {
            item {
                val errorMessage = history.errorMessage
                if (errorMessage != null) {
                    ErrorMessage(errorMessage, onRetry = history::loadNextPage)
                } else {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentGold, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthenticationRow(
    item: EntrupyCaptureResult,
    onOpen: () -> Unit,
    onRetake: () -> Unit,
    onMarketEdge: () -> Unit,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.entrupyId,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = item.statusLine(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            item.properties?.brand?.displayName?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        // The SDK documentation lists other ways to know if an item has an open retake request.
        if (item.batchRequest?.customerState == "batch_open") {
            TextButton(onClick = onRetake) { Text("Retake", color = AccentGold) }
        }
        if (item.moreDetails?.catalogInsights?.supported == true) {
            TextButton(onClick = onMarketEdge) { Text("Market Edge", color = AccentGold) }
        }
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary,
        )
    }
}

/** The result header, plus the flag when the result is flagged or resolved, like iOS. */
private fun EntrupyCaptureResult.statusLine(): String {
    val header = status?.result?.display?.header ?: status?.result?.rawId.orEmpty()
    val flag = status?.flag?.id ?: EntrupyFlagId.NONE
    return if (flag == EntrupyFlagId.NONE) header else "$header (${flag.name.lowercase()})"
}

/** Scrolls, so a pull to refresh still reaches an empty or failed list. */
@Composable
private fun PullableMessage(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { content() }
}

@Composable
private fun Message(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(32.dp),
    )
}

@Composable
private fun ErrorMessage(text: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onRetry) { Text("Retry", color = AccentGold) }
    }
}

/** The SDK throws if it was not initialized, so a tap never crashes the sample. */
private fun EntrupyApp.openSafely(screen: String, open: EntrupyApp.() -> Unit) {
    runCatching { open() }.onFailure { Log.e(TAG, "Could not open $screen", it) }
}

/**
 * Each time the footer is on screen while a next page can be asked for. That turns true again once
 * a load ends, so a refresh that brings back as many items as before still pages on. Only a layout
 * that already holds the current items counts, so one drawn before a page arrived cannot ask for
 * one more. The footer is the last item and has no key: with one, the list would hold it on screen
 * while the first page lands above it.
 */
private fun LazyListState.nextPageWanted(history: AuthenticationHistory): Flow<Unit> =
    snapshotFlow {
            val layout = layoutInfo
            layout.totalItemsCount == history.items.size + 1 &&
                layout.visibleItemsInfo.lastOrNull()?.index == history.items.size &&
                !history.isLoading &&
                !history.isLastPage &&
                history.errorMessage == null
        }
        .distinctUntilChanged()
        .filter { it }
        .map {}
