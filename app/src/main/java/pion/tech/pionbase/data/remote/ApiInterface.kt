package pion.tech.pionbase.data.remote

import pion.tech.pionbase.data.model.ApiObjectResponseData
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateResponseDtoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("https://raw.githubusercontent.com/doannghia7304-dev/fake-api/refs/heads/main/categories.json")
    suspend fun getAppCategory(): ApiObjectResponseData<List<AppCategoryDtoModel>>

    @GET("https://raw.githubusercontent.com/doannghia7304-dev/fake-api/refs/heads/main/templates.json")
    suspend fun getAllTemplate(
        @Query("category_id") categoryId: String,
    ): ApiObjectResponseData<List<TemplateResponseDtoModel>>
}
