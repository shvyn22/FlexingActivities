package shvyn22.flexingactivities.domain.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val DispatcherModule = module {
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("default")) { Dispatchers.Default }
}