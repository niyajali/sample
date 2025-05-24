package template.core.base.datastore.validation

/**
 * Validation layer for data store operations.
 */
interface DataStoreValidator {
    fun validateKey(key: String): Result<Unit>
    fun <T> validateValue(value: T): Result<Unit>
}