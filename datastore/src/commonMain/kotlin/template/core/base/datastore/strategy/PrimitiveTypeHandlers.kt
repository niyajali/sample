package template.core.base.datastore.strategy

import com.russhwolf.settings.Settings

class IntTypeHandler : TypeHandler<Int> {
    override suspend fun put(settings: Settings, key: String, value: Int): Result<Unit> {
        println("[IntTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putInt(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: Int): Result<Int> {
        val result = runCatching { settings.getInt(key, default) }
        println("[IntTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is Int
}

class StringTypeHandler : TypeHandler<String> {
    override suspend fun put(settings: Settings, key: String, value: String): Result<Unit> {
        println("[StringTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putString(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: String): Result<String> {
        val result = runCatching { settings.getString(key, default) }
        println("[StringTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is String
}

class BooleanTypeHandler : TypeHandler<Boolean> {
    override suspend fun put(settings: Settings, key: String, value: Boolean): Result<Unit> {
        println("[BooleanTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putBoolean(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: Boolean): Result<Boolean> {
        val result = runCatching { settings.getBoolean(key, default) }
        println("[BooleanTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is Boolean
}

class LongTypeHandler : TypeHandler<Long> {
    override suspend fun put(settings: Settings, key: String, value: Long): Result<Unit> {
        println("[LongTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putLong(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: Long): Result<Long> {
        val result = runCatching { settings.getLong(key, default) }
        println("[LongTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is Long
}

class FloatTypeHandler : TypeHandler<Float> {
    override suspend fun put(settings: Settings, key: String, value: Float): Result<Unit> {
        println("[FloatTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putFloat(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: Float): Result<Float> {
        val result = runCatching { settings.getFloat(key, default) }
        println("[FloatTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is Float
}

class DoubleTypeHandler : TypeHandler<Double> {
    override suspend fun put(settings: Settings, key: String, value: Double): Result<Unit> {
        println("[DoubleTypeHandler] put: key=$key, value=$value")
        return runCatching { settings.putDouble(key, value) }
    }

    override suspend fun get(settings: Settings, key: String, default: Double): Result<Double> {
        val result = runCatching { settings.getDouble(key, default) }
        println("[DoubleTypeHandler] get: key=$key, result=$result")
        return result
    }

    override fun canHandle(value: Any?): Boolean = value is Double
}