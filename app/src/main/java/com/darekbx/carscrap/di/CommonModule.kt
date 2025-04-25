package com.darekbx.carscrap.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.carscrap.domain.DataCountUseCase
import com.darekbx.carscrap.domain.DeleteCarModelUseCase
import com.darekbx.carscrap.domain.FetchChartDataUseCase
import com.darekbx.carscrap.domain.FetchFilterInfoUseCase
import com.darekbx.carscrap.domain.FetchFiltersUseCase
import com.darekbx.carscrap.domain.FetchListDataUseCase
import com.darekbx.carscrap.domain.FilteringUseCase
import com.darekbx.carscrap.domain.SaveFilterInfoUseCase
import com.darekbx.carscrap.domain.SaveFilterUseCase
import com.darekbx.carscrap.repository.SynchronizeBus
import com.darekbx.carscrap.repository.local.CacheDatabase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dao.FilterInfoDao
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.repository.remote.TimeProvider
import com.darekbx.carscrap.repository.remote.scrap.FilterFetch
import com.darekbx.carscrap.repository.remote.scrap.FilterVerification
import com.darekbx.carscrap.repository.remote.scrap.ModelGenerations
import com.darekbx.carscrap.ui.charts.ChartsViewModel
import com.darekbx.carscrap.ui.filter.CarMakes
import com.darekbx.carscrap.ui.filter.FilterViewModel
import com.darekbx.carscrap.ui.filtering.FilteringViewModel
import com.darekbx.carscrap.ui.list.ListViewModel
import com.darekbx.carscrap.ui.main.MainViewModel
import com.darekbx.carscrap.ui.synchronization.SynchronizeViewModel
import com.darekbx.carscrap.utils.DateTimeFormatter
import com.darekbx.carscrap.worker.KoinWorkerFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "car_scrap_preferences")

val appModule = module {
    single<CacheDatabase> {
        Room
            .databaseBuilder(get<Application>(), CacheDatabase::class.java, CacheDatabase.DB_NAME)
            .addMigrations(
                CacheDatabase.MIGRATION_1_2,
                CacheDatabase.MIGRATION_2_3,
                CacheDatabase.MIGRATION_3_4
            )
            .build()
    }
    single<CarModelDao> { get<CacheDatabase>().carModelDao() }
    single<FilterDao> { get<CacheDatabase>().filterDao() }
    single<FilterInfoDao> { get<CacheDatabase>().filterInfoDao() }

    single { CarMakes() }
    single { TimeProvider() }
    single { DateTimeFormatter() }
    single { androidContext().dataStore }
    single { SynchronizeBus() }
    single { Gson() }

    factory { RemoteData(get(), get(), get(), get(), get() /*, limit = 25*/) }

    factory { OkHttpClient() }
    factory { FilterVerification(get(), get()) }
    factory { ModelGenerations(get(), get()) }
    factory { FilterFetch(get(), get(), get(), get()) }

    single { KoinWorkerFactory(get(), get()) }
}

val viewModelModule = module {
    viewModel { SynchronizeViewModel(get()) }
    viewModel { ChartsViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { FilterViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { FilteringViewModel(get(), get(), get()) }
    viewModel { ListViewModel(get(), get()) }
}

val domainModule = module {
    factory { FetchChartDataUseCase(get()) }
    factory { DataCountUseCase(get()) }
    factory { SaveFilterUseCase(get()) }
    factory { FetchFiltersUseCase(get()) }
    factory { FilteringUseCase(get()) }
    factory { FetchFilterInfoUseCase(get()) }
    factory { SaveFilterInfoUseCase(get()) }
    factory { FetchListDataUseCase(get()) }
    factory { DeleteCarModelUseCase(get()) }
}
