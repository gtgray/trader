package tk.atna.tradernet.internal

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tk.atna.tradernet.model.Quote
import tk.atna.tradernet.network.ApiClient
import tk.atna.tradernet.stuff.Log

class QuotesViewModel(context: Application) : AndroidViewModel(context) {

    private val repository: QuotesRepository
    private var liveQuotes: MutableLiveData<Resource<List<Quote>>>? = null


    init {
        repository = QuotesRepository(ApiClient.getInstance())
    }

    @CallSuper
    override fun onCleared() {
        repository.clear()
    }

    fun pullQuotes(): LiveData<Resource<List<Quote>>> {

        Log.w("---------------- QUOTES $liveQuotes")

        if(liveQuotes == null) {
            liveQuotes = MutableLiveData()
            viewModelScope.launch {
                repository.pullQuotes(viewModelScope, liveQuotes!!)
            }
        }
        //
        return liveQuotes!!
    }

}