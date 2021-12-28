package dev.erickvieira.ppcc.service.user.domain.service

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.domain.exception.*
import dev.erickvieira.ppcc.service.user.domain.extension.*
import dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.UserRabbitDispatcherPort
import dev.erickvieira.ppcc.service.user.domain.repository.UserRepository
import dev.erickvieira.ppcc.service.user.extension.*
import dev.erickvieira.ppcc.service.user.web.api.UserApiDelegate
import dev.erickvieira.ppcc.service.user.web.api.model.*
import io.swagger.annotations.Api
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Service
@Api(value = "User", description = "the User API", tags = ["User"])
class UserService(
    private val userRepository: UserRepository,
    private val userDispatcher: UserRabbitDispatcherPort
) : UserApiDelegate {

    private var logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    @Throws(
        UserNotFoundException::class,
        UnexpectedException::class
    )
    override fun searchUsers(
        fullName: String?,
        cpf: String?,
        page: Int,
        size: Int,
        sort: UserFields,
        direction: Direction
    ): ResponseEntity<PageUserDTO> = logger.executeOrLog {
        val method = "method" to "searchUsers"
        val search = arrayOf(
            "cpf" to cpf,
            "fullName" to fullName,
            "page" to page,
            "size" to size,
            "sort" to sort,
            "direction" to direction
        )
        val pageable = PageRequest(pagination = search.toMap())
        logger.custom.info(method, *search)

        val pagedResult = if (cpf == null && fullName == null) {
            userRepository.findAllByDeletedAtIsNull(pageable = pageable)
        } else if (cpf != null && fullName != null) {
            userRepository.findAllByCpfAndFullNameAndDeletedAtIsNull(
                cpf = cpf,
                fullName = fullName,
                pageable = pageable
            )
        } else if (cpf != null) {
            userRepository.findAllByCpfAndDeletedAtIsNull(cpf = cpf, pageable = pageable)
        } else userRepository.findAllByFullNameAndDeletedAtIsNull(fullName = fullName!!, pageable = pageable)

        logger.custom.info(method, "totalElements" to pagedResult.totalElements, "totalPages" to pagedResult.totalPages)

        if (pagedResult.isEmpty) throw UserNotFoundException(search = search)

        ResponseEntity.ok(load { fromPage(page = pagedResult) })
    }

    @Throws(
        DuplicatedCpfException::class,
        NullPayloadException::class,
        UnexpectedException::class
    )
    override fun createUser(userCreationDTO: UserCreationDTO?): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "createUser"
        userCreationDTO.ensurePayloadNotNull { payload ->
            logger.custom.info(method, *payload.toPairArray())
            User.fromUserCreationDTO(input = payload).ensureCpfUniquenessForTheGivenUser { user ->
                userRepository.save(user).let { savedUser ->
                    userDispatcher.dispatch(userId = savedUser.id!!)
                    ResponseEntity.created(
                        ServletUriComponentsBuilder
                            .fromCurrentRequestUri()
                            .path("/${savedUser.id}")
                            .build()
                            .toUri()
                    ).body(savedUser.toUserDTO())
                }
            }
        }
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
        UnexpectedException::class
    )
    override fun retrieveUserByCpf(userCpf: String): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "retrieveUserByCpf"
        logger.custom.info(method, "cpfUser" to userCpf)
        userRepository.findFirstByCpfAndDeletedAtIsNull(userCpf)?.let {
            ResponseEntity.ok(it.toUserDTO())
        } ?: throw UserNotFoundException("cpf" to userCpf)
    }

    @Throws(
        UserNotFoundException::class,
        NullPayloadException::class,
        UnexpectedException::class
    )
    override fun partiallyUpdateUser(
        userId: UUID,
        userPartialUpdateDTO: UserPartialUpdateDTO?
    ): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "partiallyUpdateUser"
        userPartialUpdateDTO.ensurePayloadNotNull { payload ->
            logger.custom.info(method, "userId" to userId)
            logger.custom.info(method, *payload.toPairArray())
            userRepository.findFirstByIdAndDeletedAtIsNull(id = userId)?.let { user ->
                userRepository
                    .save(user.withUpdatedValues(values = payload))
                    .let { ResponseEntity.ok(it.toUserDTO()) }
            } ?: throw UserNotFoundException("id" to userId)
        }
    }

    @Throws(
        UserNotFoundException::class,
        NullPayloadException::class,
        UnexpectedException::class
    )
    override fun updateUser(
        userId: UUID,
        userUpdateDTO: UserUpdateDTO?
    ): ResponseEntity<UserDTO> = logger.executeOrLog {
        val method = "method" to "updateUser"
        userUpdateDTO.ensurePayloadNotNull { payload ->
            logger.custom.info(method, "userId" to userId)
            logger.custom.info(method, *payload.toPairArray())
            userRepository.findFirstByIdAndDeletedAtIsNull(id = userId)?.let { user ->
                userRepository
                    .save(user.withUpdatedValues(values = payload))
                    .let { ResponseEntity.ok(it.toUserDTO()) }
            } ?: throw UserNotFoundException("id" to userId)
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

    @Throws(
        NullPayloadException::class
    )
    private fun <T : Any, S : Any> T?.ensurePayloadNotNull(
        payload: String = User::class.java.name,
        callback: (it: T) -> S
    ) = this?.let {
        callback(it)
    } ?: throw NullPayloadException(payload = payload)

    @Throws(
        DuplicatedCpfException::class,
        UnexpectedException::class
    )
    private fun <T : Any> User.ensureCpfUniquenessForTheGivenUser(callback: ((it: User) -> T)): T =
        this.takeIf { userRepository.findFirstByCpf(cpf = cpf) == null }
            ?.let { callback(it) }
            ?: throw DuplicatedCpfException(cpf = cpf)

}