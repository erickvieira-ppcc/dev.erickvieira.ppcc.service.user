package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

open class BaseException constructor(
    @Transient override var message: String,
    val type: ApiErrorType? = null
) : RuntimeException()
