package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

class UserNotFoundException(vararg search: Pair<String, Any?>) : NotFoundException(
    message = "No users found using the search terms provided",
    type = ApiErrorType.USER_NOT_FOUND,
    search = search
)
