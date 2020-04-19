package br.com.licenseplate.interactor

import android.content.Context
import br.com.licenseplate.data_class.Authorization
import br.com.licenseplate.repository.StamperRepository

class StamperInteractor(private val context: Context) {
    private val repository = StamperRepository(context)

    fun authorizationList(callback: (result: Array<Authorization>) -> Unit) {
        repository.authorizationList(callback)
    }
}