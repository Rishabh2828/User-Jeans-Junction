package com.scoutandguide.jeansjunction.model

data class OrderId(
    val id: String,
    val orderDate: String? = null,
    val itemStatus: String? = null,
    val orderTimestamp: Long? = null
)
