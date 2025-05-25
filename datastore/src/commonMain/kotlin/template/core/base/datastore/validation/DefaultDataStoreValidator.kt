package template.core.base.datastore.validation

/**
 * Default implementation of DataStoreValidator that always returns valid.
 */
class DefaultDataStoreValidator : DataStoreValidator {

    override fun validateKey(key: String): Result<Unit> {
        return when {
            key.isBlank() -> Result.failure(
                IllegalArgumentException("Key cannot be blank"),
            )

            key.length > 255 -> Result.failure(
                IllegalArgumentException("Key length cannot exceed 255 characters"),
            )

            else -> Result.success(Unit)
        }
    }

    override fun <T> validateValue(value: T): Result<Unit> {
        return when (value) {
            null -> Result.failure(
                IllegalArgumentException("Value cannot be null"),
            )

            is String -> {
                if (value.length > 10000) {
                    Result.failure(
                        IllegalArgumentException("String value too large"),
                    )
                } else Result.success(Unit)
            }

            else -> Result.success(Unit)
        }
    }
}