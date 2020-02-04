/*
 * Copyright (c) 2020 OlehKapustianov.
 */

package com.olehka.datakick.repository

import androidx.lifecycle.LiveData
import com.olehka.datakick.repository.local.LocalRepository
import com.olehka.datakick.repository.model.Product
import com.olehka.datakick.repository.remote.RemoteRepository
import com.olehka.datakick.utilities.ConnectivityAgent
import java.util.concurrent.Executor

class ProductsRepository(
        private val connectivityAgent: ConnectivityAgent,
        private val localRepository: LocalRepository,
        private val remoteRepository: RemoteRepository,
        private val executor: Executor) {

    fun getProducts(): LiveData<List<Product>> {
        refreshProducts()
        return localRepository.getProducts()
    }

    fun refreshProducts() {
        if (!connectivityAgent.isDeviceConnectedToInternet())
            return

        executor.execute {
            val response = remoteRepository.getProducts().execute()
            response.body()?.toTypedArray()?.let {
                localRepository.saveProducts(it)
            }
        }
    }
}