package pion.datlt.libads.iap.utils

import android.content.Context
import com.google.gson.Gson
import pion.datlt.libads.iap.model.IapIdModel
import java.io.InputStream
import kotlin.collections.addAll

object Utils {
    fun getStringAssetFile(
        path: String,
        activity: Context,
    ): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = activity.assets.open(path)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    fun getDataInput(
        context: Context,
        nameFile: String,
    ): List<IapIdModel> {
        val listIap = mutableListOf<IapIdModel>()
        try {
            val data = getStringAssetFile(nameFile, context)
            val iapModel = Gson().fromJson(data, Array<IapIdModel>::class.java)
            listIap.addAll(iapModel)
        } catch (e: Exception) {
        }
        return listIap
    }

    fun getDataInput(
        context: Context,
        idRes: Int,
    ): List<IapIdModel> {
        val listIap = mutableListOf<IapIdModel>()
        try {
            val data = readRawTextFile(context = context, resId = idRes)
            val iapModel = Gson().fromJson(data, Array<IapIdModel>::class.java)
            listIap.addAll(iapModel)
        } catch (e: Exception) {
        }
        return listIap
    }

    fun readRawTextFile(
        context: Context,
        resId: Int,
    ): String =
        context.resources
            .openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }
}
