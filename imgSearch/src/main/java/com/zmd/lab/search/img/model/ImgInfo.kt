package com.zmd.lab.search.img.model

data class ImgInfo(
    val id: String,
    var title: String = "",
    var link: String = "",
    var thumb: String = "",
    var dm: String = "",
    var origin: String = ""
) {
    override fun toString(): String {
        return "ImgInfo(id='$id', title='$title', link='$link', thumb='$thumb', dm='$dm', origin='$origin')"
    }
}