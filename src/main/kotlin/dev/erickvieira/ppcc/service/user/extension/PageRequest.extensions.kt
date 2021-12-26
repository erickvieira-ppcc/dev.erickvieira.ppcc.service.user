package dev.erickvieira.ppcc.service.user.extension

import dev.erickvieira.ppcc.service.user.web.api.model.Direction
import dev.erickvieira.ppcc.service.user.web.api.model.UserFields
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

fun PageRequest(
    pagination: Map<String, Any?>
): PageRequest = PageRequest.of(
    pagination["page"] as Int? ?: 0,
    pagination["size"] as Int? ?: 20,
    Sort
        .by((pagination["sort"] as UserFields? ?: UserFields.fullName).value)
        .let { if (pagination["direction"] == Direction.desc) it.descending() else it.ascending() }
)