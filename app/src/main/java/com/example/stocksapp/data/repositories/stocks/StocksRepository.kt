package com.example.stocksapp.data.repositories.stocks

import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val IEXService: IEXService
) {

}