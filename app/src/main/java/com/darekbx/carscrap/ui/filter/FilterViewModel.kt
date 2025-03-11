package com.darekbx.carscrap.ui.filter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.repository.remote.scrap.FilterFetch
import com.darekbx.carscrap.repository.remote.scrap.FilterVerification
import com.darekbx.carscrap.repository.remote.scrap.ModelGenerations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterViewModel(
    private val filterVerification: FilterVerification,
    private val modelGenerations: ModelGenerations,
    private val filterFetch: FilterFetch
) : ViewModel() {

    fun verify(make: String, model: String) {
        viewModelScope.launch {
            //val response = filterVerification.verify(make, model)
            //Log.v("FilterVerification", "Count: ${response.data.advertSearch.totalCount}")

            //val g = modelGenerations.fetchGenerations(make, model)

            withContext(Dispatchers.IO) {
                //filterFetch.addfilter()
                filterFetch.fetch("cc8a015d-b871-4420-8dda-6fd146922ec6")
            }
        }
    }
}