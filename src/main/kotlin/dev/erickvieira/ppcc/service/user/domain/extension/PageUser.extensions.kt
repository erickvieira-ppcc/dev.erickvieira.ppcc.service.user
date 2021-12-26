package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.PageUserDTO
import org.springframework.data.domain.Page

fun PageUserDTO?.fromPage(page: Page<User>) = this ?: PageUserDTO(
    pageable = page.pageable,
    content = page.content.map { it.toUserDTO() },
    total = page.totalElements,
    pageCount = page.totalPages,
    sortedBy = page.pageable.sort.toString()
)