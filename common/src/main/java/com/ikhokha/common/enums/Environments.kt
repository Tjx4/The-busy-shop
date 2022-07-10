package com.ikhokha.common.enums

enum class Environments(var url: String) {
    Production("https://ssl.dstv.com/"),
    Staging("https://1j754pelwi.execute-api.eu-west-1.amazonaws.com/")
}