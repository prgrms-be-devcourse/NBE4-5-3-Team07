package com.java.NBE4_5_3_7.domain.news.entity

import com.fasterxml.jackson.annotation.JsonProperty

class NewsItem {
    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("originallink")
    var originallink: String? = null

    @JsonProperty("link")
    var link: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("pubDate")
     var pubDate: String? = null
}
