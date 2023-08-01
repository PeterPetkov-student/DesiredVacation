package com.example.desiredvacationsapp.viewmodel

import androidx.lifecycle.*
import com.example.desiredvacationsapp.daoObject.VacationDao
import com.example.desiredvacationsapp.models.Vacation
import kotlinx.coroutines.launch

class VacationViewModel(private val vacationDao: VacationDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allVacations: LiveData<List<Vacation>> = vacationDao.allVacations().asLiveData()

    private var imageName: String? = null

    fun setSelectedImageName(name: String?) {
        imageName = name
    }

    fun updateVacation(
        id: Int,
        name:String,
        hotelName: String,
        location: String,
        necessaryMoneyAmount:String,
        description:String
    ) {
        val updatedVacation = getUpdatedVacationEntry(id, name, hotelName, location, necessaryMoneyAmount, description,imageName)
        updateVacation(updatedVacation)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateVacation(item: Vacation) {
        viewModelScope.launch {
            vacationDao.update(item)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewVacation(name:String,
                       hotelName: String,
                       location: String,
                       necessaryMoneyAmount:String,
                       description:String,
                       imageUri: String?
    ) {
        val newVacation = getNewVacationEntry(name, hotelName, location, necessaryMoneyAmount, description,imageName)
        insertVacation(newVacation)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertVacation(item: Vacation) {
        viewModelScope.launch {
            vacationDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteVacation(item: Vacation) {
        viewModelScope.launch {
            vacationDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveVacation(id: Int): LiveData<Vacation> {
        return vacationDao.getItem(id).asLiveData()
    }


    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(name:String,
                     hotelName: String,
                     location: String,
                     necessaryMoneyAmount: String,
                     description:String): Boolean {
        if (name.isBlank() || hotelName.isBlank()|| necessaryMoneyAmount.isBlank() || location.isBlank() || description.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Vacation] entity class with the item info entered by the user.
     * This will be used to add a new entry to the City database.
     */
    private fun getNewVacationEntry(name:String,
                                hotelName: String,
                                location: String,
                                necessaryMoneyAmount:String,
                                description:String,
                                    imageName: String?
    ): Vacation {
        return Vacation(
            name = name,
            hotelName = hotelName,
            location = location,
            necessaryMoneyAmount = necessaryMoneyAmount.toDouble(),
            description = description,
            imageName = imageName
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Vacation] entity class with the item info updated by the user.
     */
    private fun getUpdatedVacationEntry(
        id: Int,
        name:String,
        hotelName: String,
        location: String,
        necessaryMoneyAmount:String,
        description:String,
        imageName: String?
    ): Vacation {
        return Vacation(
            id = id,
            name = name,
            hotelName = hotelName,
            location = location,
            necessaryMoneyAmount = necessaryMoneyAmount.toDouble(),
            description = description,
            imageName = imageName
        )
    }
}


/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class VacationViewModelFactory(private val vacationDao: VacationDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VacationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VacationViewModel(vacationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}