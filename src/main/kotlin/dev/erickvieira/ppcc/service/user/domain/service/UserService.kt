package dev.erickvieira.ppcc.service.user.domain.service

import dev.erickvieira.ppcc.service.user.domain.exception.*
import dev.erickvieira.ppcc.service.user.domain.extension.*
import dev.erickvieira.ppcc.service.user.domain.repository.UserRepository
import dev.erickvieira.ppcc.service.user.extension.PageRequest
import dev.erickvieira.ppcc.service.user.extension.custom
import dev.erickvieira.ppcc.service.user.extension.executeOrLog
import dev.erickvieira.ppcc.service.user.web.api.UserApiDelegate
import dev.erickvieira.ppcc.service.user.web.api.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) : UserApiDelegate {

    private var logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    @Throws(
        UserNotFoundException::class,
        UnexpectedException::class
    )
    override fun searchUsers(
        name: String?,
        cpf: String?,
        page: Int?,
        size: Int?,
        sort: String?,
        direction: Direction?
    ): ResponseEntity<PageUserDTO> = logger.executeOrLog {
        val method = "method" to "searchUsers"
        val search = arrayOf(
            "cpf" to cpf,
            "name" to name,
            "page" to page,
            "size" to size,
            "sort" to sort,
            "direction" to direction
        )
        val pageable = PageRequest(pagination = search.toMap())
        logger.custom.info(method, *search)

        val pagedResult = if (cpf == null && name == null) {
            userRepository.findAllByDeletedAtIsNull(pageable = pageable)
        } else if (cpf != null && name != null) {
            userRepository.findAllByCpfAndNameAndDeletedAtIsNull(cpf = cpf, name = name, pageable = pageable)
        } else if (cpf != null) {
            userRepository.findAllByCpfAndDeletedAtIsNull(cpf = cpf, pageable = pageable)
        } else userRepository.findAllByNameAndDeletedAtIsNull(name = name!!, pageable = pageable)

        logger.custom.info(method, "totalElements" to pagedResult.totalElements, "totalPages" to pagedResult.totalPages)

        if (pagedResult.isEmpty) throw UserNotFoundException(search = search)

        ResponseEntity.ok(pagedResult.toPageUserDTO())
    }

    @Throws(
        DuplicatedCpfException::class,
        UnexpectedException::class
    )
    override fun createUser(userCreationDTO: UserCreationDTO?): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "createUser"
        userCreationDTO?.apply {
            logger.custom.info(
                method,
                "cpf" to cpf,
                "name" to name,
                "birthDate" to birthDate
            )
        }
        val user = userCreationDTO?.toUser() ?: throw InvalidPayloadException(payload = userCreationDTO)
        user.takeIf {
            userRepository.findFirstByCpf(it.cpf) == null
        }?.let {
            userRepository.save(it)
            ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequestUri().path("/${it.id}").build().toUri()
            ).body(it.toUserDTO())
        } ?: throw DuplicatedCpfException(cpf = user.cpf)
    }

    @Throws(
        UserNotFoundException::class,
        UnexpectedException::class
    )
    override fun retrieveUser(userId: UUID): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "retrieveUser"
        logger.custom.info(method, "userId" to userId)
        userRepository.findFirstByIdAndDeletedAtIsNull(userId)?.let {
            ResponseEntity.ok(it.toUserDTO())
        } ?: throw UserNotFoundException("id" to userId)
    }

    @Throws(
        UserNotFoundException::class,
        InvalidCpfException::class,
        UnexpectedException::class
    )
    override fun retrieveUserByCpf(userCpf: String?) = logger.executeOrLog {
        val method = "method" to "retrieveUserByCpf"
        logger.custom.info(method, "cpfUser" to userCpf)
        if (userCpf == null) throw InvalidCpfException(cpf = userCpf)
        userRepository.findFirstByCpfAndDeletedAtIsNull(userCpf)?.let {
            ResponseEntity.ok(it.toUserDTO())
        } ?: throw UserNotFoundException("cpf" to userCpf)
    }

    @Throws(
        UserNotFoundException::class,
        InvalidPayloadException::class,
        UnexpectedException::class
    )
    override fun partiallyUpdateUser(
        userId: UUID,
        userPartialUpdateDTO: UserPartialUpdateDTO?
    ): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "partiallyUpdateUser"
        userPartialUpdateDTO?.apply {
            logger.custom.info(
                method,
                "name" to name,
                "birthDate" to birthDate,
                "phone" to phone,
                "email" to email
            )
        }
        logger.custom.info(method, "userId" to userId)
        val user = userRepository.findFirstByIdAndDeletedAtIsNull(userId)
            ?: throw UserNotFoundException("id" to userId)
        userRepository.save(
            user.withUpdatedValues(
                values = userPartialUpdateDTO ?: throw InvalidPayloadException(payload = userPartialUpdateDTO)
            )
        ).let {
            ResponseEntity.ok(it.toUserDTO())
        }
    }

    @Throws(
        UserNotFoundException::class,
        InvalidPayloadException::class,
        UnexpectedException::class
    )
    override fun updateUser(
        userId: UUID,
        userUpdateDTO: UserUpdateDTO?
    ): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "updateUser"
        userUpdateDTO?.apply {
            logger.custom.info(
                method,
                "name" to name,
                "birthDate" to birthDate,
                "phone" to phone,
                "email" to email
            )
        }
        logger.custom.info(method, "userId" to userId)
        val user = userRepository.findFirstByIdAndDeletedAtIsNull(userId)
            ?: throw UserNotFoundException("id" to userId)
        userRepository.save(
            user.withUpdatedValues(
                values = userUpdateDTO ?: throw InvalidPayloadException(payload = userUpdateDTO)
            )
        ).let {
            ResponseEntity.ok(it.toUserDTO())
        }
    }

    @Throws(
        UserNotFoundException::class,
        UnexpectedException::class
    )
    override fun deleteUser(userId: UUID): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "deleteUser"
        logger.custom.info(method, "userId" to userId)
        userRepository.findFirstByIdAndDeletedAtIsNull(userId)?.let {
            userRepository.save(it.asDeleted())
            ResponseEntity.ok(it.toUserDTO())
        } ?: throw UserNotFoundException("id" to userId)
    }

}