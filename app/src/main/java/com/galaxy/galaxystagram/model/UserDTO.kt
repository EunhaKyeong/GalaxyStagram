package com.galaxy.galaxystagram.model

data class UserDTO(var email: String? = null,
                var profileImgUrl: String? = null,
                var explain: String? = null,
                var Following: MutableMap<String, Boolean> = HashMap(),
                var Follwer: MutableMap<String, Boolean> = HashMap())