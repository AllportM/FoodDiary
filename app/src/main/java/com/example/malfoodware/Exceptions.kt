package com.example.malfoodware

class LoginException(message: String): Exception(message){}

class FileInitError(message: String): Exception(message){}

class DivideByZeroException(message: String): Exception(message){}

class InvalidInitializationRequest(message: String): Exception(message){}

class FileFormatError(message:String): Exception(message){}


/**
 * SQL Exceptions, all implement SQL Exception
 */
abstract class SQLException(message: String): Exception("SQLError: " + message)
{
}

class SQLQueryNotFound(message: String): SQLException("No records for " + message)

class SQLInjectionError(message: String): SQLException("Injection character detected" + message)

class SQLInsertionError(message: String): SQLException("Insertiong error for " + message)