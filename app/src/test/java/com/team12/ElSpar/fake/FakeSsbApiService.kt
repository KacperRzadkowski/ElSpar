package com.team12.ElSpar.fake

import com.team12.ElSpar.api.SsbApiService
import java.time.LocalDate


//not fully implemented yet
class FakeSsbApiService: SsbApiService {
    override suspend fun getCpi(date: LocalDate): Double {
        TODO("Not yet implemented")
    }
}