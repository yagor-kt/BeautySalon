package com.example.beautysalon.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.entity.Service
import com.example.beautysalon.data.repository.ServiceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val repository: ServiceRepository
) : ViewModel() {
    val services = repository.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun add(title: String, category: String, duration: Int, price: Int) {
        if (title.isBlank() || duration <= 0 || price < 0) return
        viewModelScope.launch {
            repository.insert(
                Service(
                    title = title.trim(),
                    category = category,
                    durationMin = duration,
                    price = price
                )
            )
        }
    }

    fun delete(service: Service) {
        viewModelScope.launch {
            repository.delete(service)
        }
    }

    class Factory(
        private val repository: ServiceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(ServicesViewModel::class.java))
            return ServicesViewModel(repository) as T
        }
    }
}