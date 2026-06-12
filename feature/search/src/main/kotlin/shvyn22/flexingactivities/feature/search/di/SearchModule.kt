package shvyn22.flexingactivities.feature.search.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import shvyn22.flexingactivities.feature.search.SearchViewModel

val SearchModule = module {
    viewModelOf(::SearchViewModel)
}