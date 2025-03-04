package ru.etu.soundboard.ui.about_devs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutDevsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "About devs"
    }
    val text: LiveData<String> = _text
}