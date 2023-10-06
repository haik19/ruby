package com.tbnt.ruby.payment

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object GooglePaymentService :
    PurchasesUpdatedListener, BillingClientStateListener, ProductDetailsResponseListener {

    private var billingClient: BillingClient? = null
    private var paymentResult: (() -> Unit)? = null
    private var billingReady: (() -> Unit)? = null
    private var detailsResult: (suspend () -> Unit)? = null
    private val ownPackages = mutableListOf<String>()
    private val productDetails = mutableListOf<ProductDetails>()
    private var defaultScope: CoroutineScope? = null


    fun init(
        applicationContext: Context,
        scope: CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default),
        billingState: () -> Unit,
    ) {
        defaultScope = scope
        billingReady = billingState
        startBillingServiceConnection(applicationContext)
    }

    private fun startBillingServiceConnection(applicationContext: Context) {
        billingClient = BillingClient.newBuilder(applicationContext)
            .enablePendingPurchases()
            .setListener(this).build()
        connectToBillingService()
    }

    private fun connectToBillingService() {
        billingClient?.let {
            if (!it.isReady) {
                it.startConnection(this)
            }
        }
    }

    fun launchBillingFlow(
        activity: FragmentActivity,
        packageId: String,
        successResult: () -> Unit
    ) {
        paymentResult = successResult
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams
                        .ProductDetailsParams
                        .newBuilder()
                        .setProductDetails(productDetails.find { it.productId == packageId }
                            ?: return).build()
                )
            )
        billingClient?.launchBillingFlow(activity, flowParams.build())
    }

    suspend fun queryProductDetails(
        packageIdList: List<String>,
        successResult: suspend () -> Unit
    ) {
        detailsResult = successResult
        val paramsList = mutableListOf<QueryProductDetailsParams.Product>()
        for (packageId in packageIdList) {
            val param = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(packageId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            paramsList.add(param)
        }
        val params = QueryProductDetailsParams
            .newBuilder()
            .setProductList(paramsList)
            .build()
        billingClient?.queryProductDetailsAsync(
            params,
            this
        )
    }

    private fun queryMyProductDetails(result: (details: List<PurchaseHistoryRecord>?) -> Unit) {
        if (ownPackages.isNotEmpty()) return
        val params =
            QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        billingClient?.queryPurchaseHistoryAsync(
            params
        ) { _, productList ->
            result(productList)
        }
    }

    fun isPackagePurchased(id: String) = ownPackages.contains(id)

    fun endBilling() {
        billingClient?.endConnection()
        paymentResult = null
        billingReady = null
        defaultScope = null
        detailsResult = null
    }

    override fun onPurchasesUpdated(result: BillingResult, p1: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            paymentResult?.invoke()
        }
    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        billingReady?.invoke()
        queryMyProductDetails { historyRecordList ->
            ownPackages.clear()
            ownPackages.addAll(historyRecordList?.map { it.products.getOrNull(0).orEmpty() }
                .orEmpty())

        }
    }

    fun getPrice(productId: String) =
        productDetails.find { it.productId == productId }?.oneTimePurchaseOfferDetails?.formattedPrice.orEmpty()

    override fun onProductDetailsResponse(
        p0: BillingResult,
        products: MutableList<ProductDetails>
    ) {
        if (products.isNotEmpty()) {
            productDetails.clear()
            productDetails.addAll(products)
        }
        defaultScope?.launch {
            detailsResult?.invoke()
        }
    }

}