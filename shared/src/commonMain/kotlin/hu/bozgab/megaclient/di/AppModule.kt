package hu.bozgab.megaclient.di

import hu.bozgab.megaclient.api.AuthenticationApi
import hu.bozgab.megaclient.api.NoteApi
import hu.bozgab.megaclient.api.ShoppingListApi
import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.config.createHttpClient
import hu.bozgab.megaclient.repository.NoteRepository
import hu.bozgab.megaclient.repository.ShoppingListRepository
import hu.bozgab.megaclient.repository.UserRepository
import hu.bozgab.megaclient.service.NotificationService
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.NoteModel
import hu.bozgab.megaclient.ui.model.SettingsModel
import hu.bozgab.megaclient.ui.model.ShoppingListModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient { get<UserStorage>().user?.token } }

    single { AuthenticationApi(get()) }
    single { UserApi(get()) }
    single { NoteApi(get()) }
    single { ShoppingListApi(get()) }

    single { UserRepository(get(), get()) }
    single { NoteRepository(get()) }
    single { ShoppingListRepository(get()) }

    single { NotificationService() }
    single { UserStorage(get()) }

    single { SettingsModel(get(), get()) }
    single { NoteModel(get(), get(), get()) }
    single { ShoppingListModel(get(), get()) }
}
