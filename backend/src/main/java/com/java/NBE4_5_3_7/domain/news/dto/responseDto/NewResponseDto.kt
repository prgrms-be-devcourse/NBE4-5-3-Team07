package com.java.NBE4_5_3_7.domain.news.dto.responseDto

import com.fasterxml.jackson.annotation.JsonProperty
import com.java.NBE4_5_3_7.domain.news.entity.NewsItem

class NewResponseDto {
    @JsonProperty("lastBuildDate")
    var lastBuildDate: String? = null

    @JsonProperty("total")
    var total = 0

    @JsonProperty("start")
    var start = 0

    @JsonProperty("display")
    var display = 0

    @JsonProperty("items")
    var items: List<NewsItem>? = null
}
