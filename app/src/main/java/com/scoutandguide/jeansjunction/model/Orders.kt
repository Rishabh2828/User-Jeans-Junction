package com.scoutandguide.jeansjunction.model

data class Orders(

    var productTitle: String? = null,
    var userId: String? = null,
    var productCategory: String? = null,
    var productImage: String? = null,
    var productRandomId: String? = null,
    var productRandomKey: String? = null,
    var price: Double? = null,
    var sizeSelected: String? = null,
    var productCount: Int? = null,
    var name: String? = null,
    var number: String? = null,
    var address: String? = null,
    val orderDate: String? = null,
    val orderTime: String? = null,
    val orderTimestamp: Long? = null,  // Timestamp field


    val itemStatus: Int? = null,




    )
