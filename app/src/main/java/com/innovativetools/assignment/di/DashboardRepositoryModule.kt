package com.innovativetools.assignment.di

import com.innovativetools.assignment.data.network.api.ApiServiceInterface
import com.innovativetools.assignment.data.repository.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DashboardRepositoryModule {

    @Provides
    @Singleton
    fun provideDashboardRepository(apiService: ApiServiceInterface): DashboardRepository {
        return DashboardRepository(apiService)
    }
}
