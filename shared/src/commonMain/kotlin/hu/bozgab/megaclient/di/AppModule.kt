package hu.bozgab.megaclient.di

import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.config.createHttpClient
import hu.bozgab.megaclient.repository.UserRepository
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.SettingsModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient { get<UserStorage>().user?.token } }

    single { UserApi(get()) }

    single { UserRepository(get()) }

    single { UserStorage(get()) }

    single { SettingsModel(get()) }
}
