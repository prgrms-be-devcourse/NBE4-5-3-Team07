package com.java.NBE4_5_3_7.domain.news.dto.responseDto

import com.fasterxml.jackson.annotation.JsonProperty
import com.java.NBE4_5_3_7.domain.news.entity.NewsItem
import lombok.Data
import lombok.Getter
import lombok.Setter

@Data
@Getter
@Setter
class NewResponseDto {
    @JsonProperty("lastBuildDate")
    private val lastBuildDate: String? = null

    @JsonProperty("total")
    private val total = 0

    @JsonProperty("start")
    private val start = 0

    @JsonProperty("display")
    private val display = 0

    @JsonProperty("items")
    private val items: List<NewsItem>? = null
}
