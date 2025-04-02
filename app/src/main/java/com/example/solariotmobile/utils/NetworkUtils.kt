package com.example.solariotmobile.utils

import java.net.InetAddress

object NetworkUtils {

    fun isValidIpAddress(ip: String): Boolean {
        return try {
            val address = InetAddress.getByName(ip)
            address.hostAddress == ip && address is java.net.Inet4Address
        } catch (e: Exception) {
            false
        }
    }

    fun isValidPort(port: String): Boolean {
        val portNumber = port.toIntOrNull() ?: return false
        return portNumber in 0..65535
    }

    fun isDomainName(domainName: String): Boolean {
        val domainPattern = Regex("^(?!-)[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return domainPattern.matches(domainName)
    }

    /**
     * @param serverAddress server IP or domain name.
     * @param serverPort the server port, can be empty if serverAddress is a domain name.
     * @return the server URL.
     */
    fun getServerUrl(serverAddress: String, serverPort: String): String {
        return if (isDomainName(serverAddress)) serverAddress else "${serverAddress}:${serverPort}"
    }
}