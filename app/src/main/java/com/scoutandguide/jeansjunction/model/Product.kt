package com.scoutandguide.jeansjunction.model

import android.os.Parcel
import android.os.Parcelable
import kotlin.collections.ArrayList

data class Product(
    var productRandomId: String? = null,
    var productTitle: String? = null,
    var productColor: String? = null,
    var productSize: Int? = null,
    var productPrice: Int? = null,
    var productStock: Int? = null,
    var productMultipleSizes: ArrayList<String?>? = null,
    var productFabric: String? = null,
    var productCategory: String? = null,
    var productDetails: String? = null,
    var productRealImageUris: ArrayList<String?>? = null, // Added field

    var itemCount: Int? = null,
    var adminUid: String? = null,
    var productImageUris: ArrayList<String?>? = null,
    var productType: String? = null,
    var productBestSeller: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(), // Handle productRealImageUris
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productRandomId)
        parcel.writeString(productTitle)
        parcel.writeString(productColor)
        parcel.writeValue(productSize)
        parcel.writeValue(productPrice)
        parcel.writeValue(productStock)
        parcel.writeStringList(productMultipleSizes)
        parcel.writeString(productFabric)
        parcel.writeString(productCategory)
        parcel.writeString(productDetails)
        parcel.writeStringList(productRealImageUris) // Handle productRealImageUris
        parcel.writeValue(itemCount)
        parcel.writeString(adminUid)
        parcel.writeStringList(productImageUris)
        parcel.writeString(productType)
        parcel.writeString(productBestSeller)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
