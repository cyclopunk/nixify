package io.nixify.api.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

interface ILoggingService {
    /**
     * Add an INFO   message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    fun i(message : String, vararg params: Any)
    /**
     * Add an ERROR message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    fun e(message : String, ex : Throwable)
    /**
     * Add an DEBUG message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    fun d(message : String, vararg params: Any)
    /**
     * Add an WARN message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    fun w(message : String, vararg params: Any)
    /**
     * Add an TRACE message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    fun t(message : String, vararg params: Any)
    
}

@Service
class BasicLoggingService : ILoggingService {
    
    private fun getLogger() : Logger {
        return LoggerFactory.getLogger(Exception().stackTrace[1].className)
    }
    
    /**
     * Add an INFO message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    override fun i(message : String, vararg params: Any) {
        getLogger() .info(message, params)
    }
    /**
     * Add an ERROR message to the log.
     *
     * @param message the log message
     * @param ex The exception that was thrown for this error.
     */
    override fun e(message : String, ex : Throwable) {
       getLogger().error(message, ex)
    }
    /**
     * Add an DEBUG message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    override fun d(message : String, vararg params: Any) {
       getLogger().debug(message, params)
    }
    /**
     * Add an WARN level message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    override fun w(message : String, vararg params: Any) {
       getLogger().warn(message, params)
    }
    /**
     * Add an TRACE level message to the log.
     *
     * @param message the log message
     * @param params any parameters for the log string.
     */
    override fun t(message : String, vararg params: Any) {
        getLogger().trace(message, params)
    }
}