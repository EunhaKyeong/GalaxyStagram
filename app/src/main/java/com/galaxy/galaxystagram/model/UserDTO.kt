package com.galaxy.galaxystagram.model

data class UserDTO(var email: String? = null,
                   var profileImgUrl: String? = null,
                   var explain: String? = null,
                   var following: MutableMap<String, Boolean> = HashMap(),
                   var follower: MutableMap<String, Boolean> = HashMap())