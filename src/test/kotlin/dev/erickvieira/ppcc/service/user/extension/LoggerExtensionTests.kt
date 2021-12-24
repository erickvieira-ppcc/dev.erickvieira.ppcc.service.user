package dev.erickvieira.ppcc.service.user.extension

import dev.erickvieira.ppcc.service.user.UserServiceApplication
import dev.erickvieira.ppcc.service.user.UserUnitTests
import dev.erickvieira.ppcc.service.user.domain.exception.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggerExtensionTests : UserUnitTests() {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceApplication::class.java)

    @Test
    fun `execureOrLog - must return the value`() {
        val user = generateUser()
        assert(user === logger.executeOrLog { user })
        val userPage = generateUserPage()
        assert(userPage === logger.executeOrLog { userPage })
    }

    @Test
    fun `executeOrLog - must return void`() = assert(Unit == logger.executeOrLog { println("Testing...") })

    @Test
    fun `execureOrLog - must throw BaseException`() {
        assertThrows<BaseException> {
            logger.executeOrLog { throw UserNotFoundException() }
        }
        assertThrows<BaseException> {
            logger.executeOrLog { throw DuplicatedCpfException(cpf = "") }
        }
        assertThrows<BaseException> {
            logger.executeOrLog { throw InvalidPayloadException(payload = null) }
        }
    }

    @Test
    fun `execureOrLog - must throw UnexpectedException`() {
        assertThrows<UnexpectedException> {
            logger.executeOrLog { throw Exception() }
        }
        assertThrows<UnexpectedException> {
            logger.executeOrLog { throw Error() }
        }
    }

}