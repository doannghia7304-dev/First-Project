package pion.datlt.libads.iap

import pion.datlt.libads.iap.model.ProductModel

interface SubscribeInterface {
    fun subscribeSuccess(productModel: ProductModel)
    fun subscribeError(error: String)
}
