package com.example.libiap

import com.example.libiap.model.ProductModel

interface SubscribeInterface {
    fun subscribeSuccess(productModel: ProductModel)
    fun subscribeError(error: String)
}
