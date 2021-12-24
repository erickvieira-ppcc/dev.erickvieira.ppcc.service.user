package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.PageUserDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

fun Page<User>.toPageUserDTO(): PageUserDTO = PageUserDTO()
    .pageable(pageable)
    .content(content.map { it.toUserDTO() })
    .total(totalElements)
    .pageCount(totalPages)
    .currentPage(pageable.pageNumber)
    .pageSize(pageable.pageSize)