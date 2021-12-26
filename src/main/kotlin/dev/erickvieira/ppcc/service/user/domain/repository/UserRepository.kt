package dev.erickvieira.ppcc.service.user.domain.repository

import dev.erickvieira.ppcc.service.user.domain.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findFirstByCpf(cpf: String): User?

    fun findFirstByIdAndDeletedAtIsNull(id: UUID): User?

    fun findFirstByCpfAndDeletedAtIsNull(cpf: String): User?

    @Query(
        value = "FROM User u WHERE u.deletedAt IS NULL",
        countQuery = """
            SELECT      COUNT(u.id) 
            FROM        User u 
            WHERE       u.deletedAt IS NULL
        """
    )
    fun findAllByDeletedAtIsNull(pageable: Pageable): Page<User>

    @Query(
        value = "FROM User u WHERE u.cpf = ?1 AND u.deletedAt IS NULL",
        countQuery = """
            SELECT      COUNT(u.id) 
            FROM        User u 
            WHERE       u.cpf = ?1
                        AND u.deletedAt IS NULL
        """
    )
    fun findAllByCpfAndDeletedAtIsNull(cpf: String, pageable: Pageable): Page<User>

    @Query(
        value = "FROM User u WHERE (LOWER(u.fullName) LIKE ('%' || LOWER(?1) || '%')) AND u.deletedAt IS NULL",
        countQuery = """
            SELECT      COUNT(u.id) 
            FROM        User u 
            WHERE       (LOWER(u.fullName) LIKE ('%' || LOWER(?1) || '%')) 
                        AND u.deletedAt IS NULL
        """
    )
    fun findAllByFullNameAndDeletedAtIsNull(fullName: String, pageable: Pageable): Page<User>

    @Query(
        value = """
            FROM        User u 
            WHERE       u.cpf = ?1
                        AND (LOWER(u.fullName) LIKE ('%' || LOWER(?2) || '%'))
                        AND u.deletedAt IS NULL
        """,
        countQuery = """
            SELECT      COUNT(u.id) 
            FROM        User u 
            WHERE       u.cpf = ?1
                        AND (LOWER(u.fullName) LIKE ('%' || LOWER(?2) || '%'))
                        AND u.deletedAt IS NULL
        """
    )
    fun findAllByCpfAndFullNameAndDeletedAtIsNull(cpf: String, fullName: String, pageable: Pageable): Page<User>

}