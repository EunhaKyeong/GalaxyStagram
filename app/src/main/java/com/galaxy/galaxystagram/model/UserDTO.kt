package com.galaxy.galaxystagram.model

data class UserDTO(var uid: String? = null,
                   var email: String? = null,
                   var profileImgUrl: String? = null,
                   var explain: String? = null,
                   var followers: MutableMap<String, Boolean> = HashMap(),
                   var followings: MutableMap<String, Boolean> = HashMap())
