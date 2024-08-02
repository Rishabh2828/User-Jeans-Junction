package com.scoutandguide.jeansjunction.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.model.CartProduct
import com.scoutandguide.jeansjunction.model.Orders
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel : ViewModel() {

    private val _isProductSaved = MutableStateFlow ( false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved
    private val _isUserProfileComplete = MutableLiveData<Boolean?>()
    val isUserProfileComplete: MutableLiveData<Boolean?> = _isUserProfileComplete


    fun fetchAllTheProducts(category: Any?): Flow<List<Product>> = callbackFlow {
        val db =   FirebaseDatabase.getInstance().getReference("Admins Products").child("AllProducts")

        val eventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    if (category=="All"||prod?.productCategory==category){

                        products.add(prod!!)

                    }

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{(db.removeEventListener(eventListener))}
    }

    fun fetchBestsellerProducts(category: Any?): Flow<List<Product>> = callbackFlow {
        val db =   FirebaseDatabase.getInstance().getReference("Admins Products").child("AllProducts")

        val eventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    if (prod?.productBestSeller==category){

                        products.add(prod!!)

                    }

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{(db.removeEventListener(eventListener))}
    }



    fun fetchCategoryProducts(category: Any?, type: String?): Flow<List<Product>> = callbackFlow {
        val db =   FirebaseDatabase.getInstance().getReference("Admins Products").child("ProductCategory").child(category.toString())

        val eventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    if (prod?.productType==type){

                        products.add(prod!!)

                    }

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{(db.removeEventListener(eventListener))}
    }




    fun saveProduct(product: CartProduct) {
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart Products")
            .child("Users/${product.userId}").push() // Generate a new unique key for each entry

        product.productRandomKey = cartRef.key // Store the unique key in product

        cartRef.setValue(product).addOnSuccessListener {
            _isProductSaved.value = true
        }.addOnFailureListener {
            _isProductSaved.value = false
        }
    }


    fun deleteProductFromCart(product: CartProduct) {
        val db = FirebaseDatabase.getInstance().getReference("Cart Products")
        val userId = product.userId
        val productRandomId = product.productRandomId

        if (userId != null && productRandomId != null) {
            db.child("Users").child(userId).child(product.productRandomKey!!).removeValue()
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener {
                    // Handle failure
                }
        } else {
            Log.e("UserViewModel", "User ID or Product Random ID is null")
        }
    }

    fun fetchOrders(userId: String): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Orders").child(userId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = ArrayList<Orders>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Orders::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }



    fun checkUserProfileComplete(userId: String) {
        val userRef = Utils.getDatabseInstance().reference.child("User profiles").child(userId)
        _isUserProfileComplete.value= null

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                _isUserProfileComplete.value = user != null && !user.name.isNullOrEmpty() && !user.mobilenumber.isNullOrEmpty() && !user.address.isNullOrEmpty() && !user.pincode.isNullOrEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserViewModel", "Failed to fetch user profile", error.toException())
                _isUserProfileComplete.value = false
            }
        })


    }

    fun saveOrder(userId: String, cartProducts: List<CartProduct>, callback: (Boolean) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(userId)
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart Products").child("Users").child(userId)

        val currentTimestamp = System.currentTimeMillis()
        val orderUpdates = mutableMapOf<String, Any>()

        val orderId = ordersRef.push().key ?: return

        cartProducts.forEach { cartProduct ->
            val orderProductRef = ordersRef.child(orderId).push()
            val order = Orders(
                productTitle = cartProduct.productTitle,
                userId = cartProduct.userId,
                productCategory = cartProduct.productCategory,
                productImage = cartProduct.productImage,
                productRandomId = cartProduct.productRandomId,
                productRandomKey = orderProductRef.key,
                price = cartProduct.price,
                sizeSelected = cartProduct.sizeSelected,
                productCount = cartProduct.productCount,
                orderDate = Utils.getCurrentDate(),
                orderTime = Utils.getCurrentTime(),
                orderTimestamp = currentTimestamp,
                itemStatus = 0,
                name = "", // Replace with actual user details if available
                number = "", // Replace with actual user details if available
                address = "" // Replace with actual user details if available
            )
            orderUpdates["Admins/Orders/$userId/$orderId/${orderProductRef.key}"] = order
        }

        // Save orders to Firebase and clear cart
        FirebaseDatabase.getInstance().reference.updateChildren(orderUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Delete all items from the cart
                cartRef.removeValue().addOnCompleteListener { deleteTask ->
                    callback(deleteTask.isSuccessful)
                }
            } else {
                callback(false)
            }
        }
    }

}