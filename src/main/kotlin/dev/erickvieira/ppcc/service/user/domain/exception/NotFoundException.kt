package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

open class NotFoundException(message: String, type: ApiErrorType, vararg search: Pair<String, Any?>) : BaseException(
    message = "$message${
        search
            .filter { it.second != null }
            .takeUnless { it.isEmpty() }
            ?.joinToString(prefix = " - ", separator = ", ") { "${it.first}: ${it.second}" }
            ?: String()
    }",
    type = type
)