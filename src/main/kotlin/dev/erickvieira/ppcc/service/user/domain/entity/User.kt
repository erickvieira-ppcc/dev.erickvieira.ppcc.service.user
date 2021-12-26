package dev.erickvieira.ppcc.service.user.domain.entity

import com.google.gson.Gson
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import org.hibernate.validator.constraints.br.CPF
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_user")
data class User(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID? = null,

    @CPF
    @Column(name = "cpf", nullable = false)
    val cpf: String = String(),

    @Column(name = "full_name", nullable = false)
    val fullName: String = String(),

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDate? = null,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "email")
    val email: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime? = null,

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime? = null,

    @Column(name = "deleted_at")
    val deletedAt: OffsetDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String = Gson().toJson(this)

    companion object {}
}