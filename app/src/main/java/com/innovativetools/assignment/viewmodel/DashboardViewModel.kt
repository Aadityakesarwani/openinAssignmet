package com.innovativetools.assignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.innovativetools.assignment.data.model.ApiResponse
import com.innovativetools.assignment.data.model.ClickInfo
import com.innovativetools.assignment.data.model.Link
import com.innovativetools.assignment.data.model.RequestBody
import com.innovativetools.assignment.data.repository.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _totalClicks = MutableLiveData<String>()
    val totalClicks: LiveData<String> = _totalClicks

    private val _todayClicks = MutableLiveData<String>()
    val todayClicks: LiveData<String> = _todayClicks

    private val _totalLinks = MutableLiveData<String>()
    val totalLinks: LiveData<String> = _totalLinks

    private val _topLocation = MutableLiveData<String>()
    val topLocation: LiveData<String> = _topLocation

    private val _topSource = MutableLiveData<String>()
    val topSource: LiveData<String> = _topSource

    private val _recentLinks = MutableLiveData<List<Link>>()
    val recentLinks: LiveData<List<Link>> = _recentLinks

    private val _topLinks = MutableLiveData<List<Link>>()
    val topLinks: LiveData<List<Link>> = _topLinks

    private val _chartDataEntries = MutableLiveData<List<Entry>>()
    val chartDataEntries: LiveData<List<Entry>> = _chartDataEntries

    private val _chartLabels = MutableLiveData<List<String>>()
    val chartLabels: LiveData<List<String>> = _chartLabels

    //GET Request
    fun initialize() {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getDashboardData()
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        _greeting.value = getGreeting()
                        _totalClicks.value = response.totalClicks.toString()
                        _todayClicks.value = response.todayClicks.toString()
                        _totalLinks.value = response.totalLinks.toString()
                        _topLocation.value = response.topLocation
                        _topSource.value = response.topSource
                        _recentLinks.value = response.data.recentLinks
                        _topLinks.value = response.data.topLinks
                        _userName.value = "User Name"

                        val entries = mutableListOf<Entry>()
                        val labels = mutableListOf<String>()
                        val recentLink = response.data.recentLinks

                        for (i in recentLink.indices) {
                            val link = recentLink[i]
                            val totalClick = link.totalClicks.toFloat()
                            val entry = Entry(i.toFloat(), totalClick)
                            entries.add(entry)
                            labels.add(link.timesAgo)
                        }

                        _chartDataEntries.value = entries
                        _chartLabels.value = labels
                        _isLoading.value = false
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "Failed to fetch data"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = "Error: ${e.message}"
                }
            }
        }
    }


    //POST request
    suspend fun postData(
        apiEndpoint: String,
        requestBody: RequestBody,
    ): ApiResponse? {
        _isLoading.value = true

        return try {
            val response = repository.postData(apiEndpoint, requestBody)

            withContext(Dispatchers.Main) {
                if (response != null) {
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "Failed to fetch data"
                }
            }
            response
        } catch (e: Exception) {
            _isLoading.value = false
            _errorMessage.value = "Error: ${e.message}"
            null
        }
    }


    private fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }


}

