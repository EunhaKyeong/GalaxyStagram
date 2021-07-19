package com.galaxy.galaxystagram.model

data class ContentDTO(var exaplain: String? = null,
                      var imageUrl: String? = null,
                      var uid: String? = null,
                      var userEmail: String? = null,
                      var timestamp: Long? = null,
                      var favoriteCount: Int = 0,
                      var favorites: Map<String, Boolean> = HashMap()) {
    data class Comment(var uid: String? = null,
                       var userEmail: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}