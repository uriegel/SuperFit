package de.uriegel.superfit.requests

class HttpProtocolException(val code: Int, message: String)
    : Exception(message) {}