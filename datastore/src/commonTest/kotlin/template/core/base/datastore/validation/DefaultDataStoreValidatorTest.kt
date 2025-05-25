package template.core.base.datastore.validation

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DefaultDataStoreValidatorTest {
    private val validator = DefaultDataStoreValidator()

    @Test
    fun validateKey_AcceptsValidKey() {
        val result = validator.validateKey("validKey")
        assertTrue(result.isSuccess)
    }

    @Test
    fun validateKey_RejectsEmptyKey() {
        val result = validator.validateKey("")
        assertTrue(result.isFailure)
    }

    @Test
    fun validateValue_AcceptsNonNull() {
        val result = validator.validateValue(123)
        assertTrue(result.isSuccess)
    }

    @Test
    fun validateValue_RejectsNull() {
        val result = validator.validateValue(null)
        assertTrue(result.isFailure)
    }
} 