package com.bwasik.koin

import com.bwasik.user.respository.UserRepository
import com.bwasik.user.service.UserService
import org.koin.dsl.module

val userModule = module {
    single { UserRepository() }
    single{ UserService(userRepository = get())}
}