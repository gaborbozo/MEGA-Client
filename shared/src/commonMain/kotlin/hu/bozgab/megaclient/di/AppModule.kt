package hu.bozgab.megaclient.di

import hu.bozgab.megaclient.api.AuthenticationApi
import hu.bozgab.megaclient.api.NoteApi
import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.config.createHttpClient
import hu.bozgab.megaclient.repository.NoteRepository
import hu.bozgab.megaclient.repository.UserRepository
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.NoteModel
import hu.bozgab.megaclient.ui.model.SettingsModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient { get<UserStorage>().user?.token } }

    single { AuthenticationApi(get()) }
    single { UserApi(get()) }
    single { NoteApi(get()) }

    single { UserRepository(get(), get()) }
    single { NoteRepository(get()) }

    single { UserStorage(get()) }

    single { SettingsModel(get()) }
    single { NoteModel(get(), get()) }
}
