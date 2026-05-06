package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.AuthRepository

class AuthUseCase (
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String){
        repository.login(login, password)
    }
}