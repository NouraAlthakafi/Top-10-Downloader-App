package com.example.top10downloaderapp

data class Top(val name: String?){
    override fun toString(): String = name!!
}