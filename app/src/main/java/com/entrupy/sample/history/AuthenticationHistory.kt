package com.entrupy.sample.history

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.entrupy.sdk.app.EntrupyApp
import com.entrupy.sdk.listeners.EntrupyErrorCode
import com.entrupy.sdk.listeners.SearchAuthenticationItemsCallback
import com.entrupy.sdk.model.EntrupyCaptureResult
import com.entrupy.sdk.model.EntrupySearchItemsResult
import com.entrupy.sdk.model.EntrupySearchQuery

private const val TAG = "EntrupySampleApp"

/**
 * The past authentications of the logged in user, loaded one page at a time with
 * [EntrupyApp.searchAuthenticationItems].
 *
 * The query is kept between pages: each page sets [EntrupySearchQuery.cursor] to the
 * [EntrupySearchItemsResult.nextCursor] it returned, and a `null` cursor means the last page.
 */
class AuthenticationHistory(private val entrupyApp: EntrupyApp) {

    val items = mutableStateListOf<EntrupyCaptureResult>()

    var totalCount by mutableStateOf<Int?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLastPage by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** The first page is loading again. The old list stays on screen until it answers. */
    var isRefreshing by mutableStateOf(false)
        private set

    private var query: EntrupySearchQuery? = null

    // A page that answers after a reload belongs to the old list, so it is dropped.
    private var generation = 0

    /** Starts again from the first page, when the tab opens or the list is pulled down. */
    fun reload() {
        generation++
        isLoading = false
        isLastPage = false
        // No filters: every category, last 30 days, 20 items per page, like the iOS sample.
        query = EntrupySearchQuery()
        isRefreshing = true
        load(firstPage = true)
    }

    fun loadNextPage() = load(firstPage = false)

    private fun load(firstPage: Boolean) {
        val currentQuery = query ?: return
        if (isLoading || isLastPage) return

        isLoading = true
        errorMessage = null
        val page = generation
        entrupyApp.searchAuthenticationItems(
            query = currentQuery,
            callback =
                object : SearchAuthenticationItemsCallback {
                    override fun onSearchCompleted(result: EntrupySearchItemsResult) {
                        if (page != generation) return
                        if (firstPage) items.clear()
                        // An item that moved between pages while paging would be listed twice,
                        // and the list keys its rows by ID.
                        val listed = items.mapTo(HashSet()) { it.entrupyId }
                        items += result.items.filter { listed.add(it.entrupyId) }
                        totalCount = result.totalCount
                        currentQuery.cursor = result.nextCursor
                        isLastPage = result.nextCursor == null
                        isLoading = false
                        isRefreshing = false
                        Log.d(TAG, "Search page: ${result.items.size} of ${result.totalCount}")
                    }

                    override fun onSearchError(errorCode: Int, description: String) {
                        if (page != generation) return
                        isLoading = false
                        isRefreshing = false
                        errorMessage =
                            when (errorCode) {
                                EntrupyErrorCode.UNAUTHORIZED_ACCESS,
                                EntrupyErrorCode.FORBIDDEN ->
                                    "Authorization expired. Please login again."
                                else -> description
                            }
                        Log.e(TAG, "Search error: $description (Code: $errorCode)")
                    }
                },
        )
    }
}
