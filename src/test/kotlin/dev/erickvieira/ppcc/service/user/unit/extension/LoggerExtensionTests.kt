package dev.erickvieira.ppcc.service.user.unit.extension

import dev.erickvieira.ppcc.service.user.UserServiceApplication
import dev.erickvieira.ppcc.service.user.unit.UserUnitTests
import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.domain.exception.*
import dev.erickvieira.ppcc.service.user.extension.executeOrLog
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggerExtensionTests : UserUnitTests() {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceApplication::class.java)

    @Test
    fun `executeOrLog - must return the value`() {
        val user = User.randomize()
        assert(user === logger.executeOrLog { user })
        val userPage = generateUserPage()
        assert(userPage === logger.executeOrLog { userPage })
    }

    @Test
    fun `executeOrLog - must return void`() = assert(Unit == logger.executeOrLog { println("Testing...") })

    @Test
    fun `executeOrLog - must throw BaseException`() {
        assertThrows<BaseException> {
            logger.executeOrLog { throw UserNotFoundException() }
        }
        assertThrows<BaseException> {
            logger.executeOrLog { throw DuplicatedCpfException(cpf = "") }
        }
        assertThrows<BaseException> {
            logger.executeOrLog { throw NullPayloadException(payload = User::class.java.name) }
        }
    }

    @Test
    fun `executeOrLog - must throw UnexpectedException`() {
        assertThrows<UnexpectedException> {
            logger.executeOrLog { throw Exception() }
        }
        assertThrows<UnexpectedException> {
            logger.executeOrLog { throw Error() }
        }
    }

}