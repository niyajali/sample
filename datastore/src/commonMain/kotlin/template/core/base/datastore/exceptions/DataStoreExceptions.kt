package template.core.base.datastore.exceptions

sealed class DataStoreException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

class InvalidKeyException(key: String) : DataStoreException("Invalid key: $key")

class SerializationException(message: String, cause: Throwable? = null) :
    DataStoreException("Serialization failed: $message", cause)

class DeserializationException(message: String, cause: Throwable? = null) :
    DataStoreException("Deserialization failed: $message", cause)

class UnsupportedTypeException(type: String) :
    DataStoreException("Unsupported type: $type")

class CacheException(message: String, cause: Throwable? = null) :
    DataStoreException("Cache operation failed: $message", cause)