package com.galaxy.galaxystagram.model

data class ContentDTO(var exaplain: String? = null,     //게시글
                      var imageUrl: String? = null,     //이미지 url
                      var uid: String? = null,      //글 작성자 식별자
                      var userEmail: String? = null,    //글 작성자 이메일
                      var timestamp: Long? = null,      //작성일
                      var favoriteCount: Int = 0,       //좋아요 수
                      var favorites: MutableMap<String, Boolean> = HashMap()) {    //좋아요를 누른 사람들
    data class Comment(var uid: String? = null,     //댓글 작성자 식별자
                       var userEmail: String? = null,   //댓글 작성자 이메일
                       var comment: String? = null,     //댓글
                       var timestamp: Long? = null)     //댓글 작성일
}