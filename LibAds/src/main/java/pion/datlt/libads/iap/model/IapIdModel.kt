package pion.datlt.libads.iap.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import pion.datlt.libads.iap.utils.Utils

data class IapIdModel(
    @SerializedName("id")
    var idProduct: String = "null",

    @SerializedName("type")
    var type: String = "null"

) {
    companion object {
        fun getDataInput(context: Context, nameFile: String): List<IapIdModel> {
            val listIap = mutableListOf<IapIdModel>()

            try {
                val data = Utils.getStringAssetFile(nameFile, context)
                val ads = Gson().fromJson(data, Array<IapIdModel>::class.java)

                listIap.addAll(ads)
            } catch (e: Exception) {
            }
            return listIap
        }
    }
}