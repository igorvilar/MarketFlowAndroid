package br.com.icvilar.marketflow.di

import br.com.icvilar.marketflow.data.repository.ExchangeRepositoryImpl
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExchangeRepository(
        exchangeRepositoryImpl: ExchangeRepositoryImpl
    ): ExchangeRepository
}
