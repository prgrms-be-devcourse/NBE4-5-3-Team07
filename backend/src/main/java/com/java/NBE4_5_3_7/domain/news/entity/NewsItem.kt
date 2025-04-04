package com.java.NBE4_5_3_7.domain.news.entity

import com.fasterxml.jackson.annotation.JsonProperty

class NewsItem {
    @JsonProperty("title")
    private var title: String? = null

    @JsonProperty("originallink")
    private var originallink: String? = null

    @JsonProperty("link")
    private var link: String? = null

    @JsonProperty("description")
    private var description: String? = null

    @JsonProperty("pubDate")
    private var pubDate: String? = null
}
