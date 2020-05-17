package br.com.licenseplate.interactor

import android.content.Context
import android.location.Address
import android.text.TextUtils.indexOf
import br.com.licenseplate.dataclass.Authorization
import br.com.licenseplate.dataclass.AuthorizationClient
import br.com.licenseplate.dataclass.Client
import br.com.licenseplate.dataclass.Store
import br.com.licenseplate.repository.ClientRepository
import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ClientInteractor(val context: Context) {
    private val repository = ClientRepository(context)

    fun verifyID(root: String, callback: (result: Int) -> Unit) {
        repository.verifyID(root, callback)
    }

    fun verifyClientData(client: Client, callback: (result: String?) -> Unit) {
        val cpf = client.cpf
        if (client.nome != null && client.cpf != null && client.cel != null) {
            if (cpf?.length != 11) {
                callback("CPF")
            } else if (client.nome.isEmpty() || client.cpf.isEmpty() || client.cel.isEmpty()) {
                callback("VAZIO")
            } else {
                try {
                    cpf.toLong()

                    callback(null)
                } catch (e: Exception) {
                    callback("CPF")
                }
            }
        }
    }

    fun verifyLicenseNumber(
        authorization: Authorization,
        callback: (result: String?) -> Unit
    ) {
        if (authorization.placa?.isEmpty()!!) {
            callback("VAZIO")
        } else if (authorization.placa!!.length != 7) {
            callback("PLACA")
        } else {
            var ver = 0
            for (i in 0 until 7) {
                if ((i == 3 || i == 5 || i == 6) && !authorization.placa!![i].isDigit()) {
                    ver = 1
                    callback("PLACA")
                } else if ((i != 3 && i != 5 && i != 6) && !authorization.placa!![i].isLetter()) {
                    ver = 1
                    callback("PLACA")
                }
            }
            if (ver == 0) {
                callback(null)
            }
        }
    }

    fun verifyAuthorization(
        authorization: Authorization,
        callback: (result: String?) -> Unit
    ) {
        if (authorization.numAutorizacao?.isEmpty()!!) {
            callback("VAZIO")
        } else if (authorization.numAutorizacao!!.length < 15) {
            callback("LENGTH")
        } else {
            callback(null)
        }
    }

    fun storeList(location: LatLng, callback: (result: Array<Store>) -> Unit) {
        repository.storeList { response ->
            mergeSort(response, location, 0, response.size - 1)
            callback(response)
        }
    }

    fun getAddress(latLng: LatLng, callback: (result: List<Address>) -> Unit) {
        repository.getAddress(latLng, callback)
    }

    private fun distance(start: LatLng, end: LatLng): Double {
        val lat1 = start.latitude
        val lat2 = end.latitude
        val long1 = start.longitude
        val long2 = end.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLong = Math.toRadians(long2 - long1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
                dLong / 2
            ) * sin(dLong / 2)
        val c = 2 * asin(sqrt(a))
        return 6366000 * c
    }

    private fun mergeSort(storeArray: Array<Store>, clientLatLng: LatLng, start: Int, end: Int) {
        var middle: Int

        if (start < end) {
            middle = (start + end) / 2
            mergeSort(storeArray, clientLatLng, start, middle)
            mergeSort(storeArray, clientLatLng, middle + 1, end)
            intersperse(storeArray, clientLatLng, start, middle, end)
        }
    }

    private fun intersperse(
        storeArray: Array<Store>,
        clientLatLng: LatLng,
        start: Int,
        middle: Int,
        end: Int
    ) {
        //variáveis para cálculo de distância
        var latitude: Double?
        var longitude: Double?
        var latLng1: LatLng
        var latLng2: LatLng
        var dist1: Double
        var dist2: Double
        //Variáveis para o intersperse
        var i: Int
        var j: Int
        val aux = arrayListOf<Store>()

        i = start
        j = middle + 1

        while (i <= middle && j <= end) {
            //Faz latitude e longitude do pivo
            latitude =
                storeArray[i].localizacao?.substring(
                    0,
                    indexOf(storeArray[i].localizacao, ',')
                )
                    ?.toDouble()
            longitude =
                storeArray[i].localizacao?.substring(
                    indexOf(
                        storeArray[i].localizacao,
                        ", "
                    ) + 1
                )?.toDouble()
            latLng1 = if (latitude != null && longitude != null) {
                LatLng(latitude, longitude)
            } else {
                LatLng(0.0, 0.0)
            }

            //Faz latitude e longitude do auxiliar
            latitude =
                storeArray[j].localizacao?.substring(
                    0,
                    indexOf(storeArray[j].localizacao, ',')
                )
                    ?.toDouble()
            longitude =
                storeArray[j].localizacao?.substring(
                    indexOf(
                        storeArray[j].localizacao,
                        ", "
                    ) + 1
                )?.toDouble()
            latLng2 = if (latitude != null && longitude != null) {
                LatLng(latitude, longitude)
            } else {
                LatLng(0.0, 0.0)
            }

            dist1 = distance(clientLatLng, latLng1)
            dist2 = distance(clientLatLng, latLng2)

            if (dist1 <= dist2) {
                aux.add(storeArray[i])
                i++
            } else {
                aux.add(storeArray[j])
                j++
            }
        }

        while (i <= middle) {
            aux.add(storeArray[i])
            i++
        }

        while (j <= end) {
            aux.add(storeArray[j])
            j++
        }

        for (a in 0 until (end - start) + 1) {
            storeArray[start + a] = aux[a]
        }
    }

    fun saveAuthorization(autCli: AuthorizationClient){
        repository.save("autorizacaoCliente", autCli, autCli.id)
    }
}
