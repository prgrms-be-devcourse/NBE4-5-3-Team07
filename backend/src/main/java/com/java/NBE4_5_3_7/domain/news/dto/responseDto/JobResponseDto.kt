package com.java.NBE4_5_3_7.domain.news.dto.responseDto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.Data
import lombok.Getter

@Data
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class JobResponseDto {
    var totalCount = 0
    var result: List<Job>? = null

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Job {
        private val recrutPblntSn: Long? = null
        private val pblntInstCd: String? = null
        private val pbadmsStdInstCd: String? = null
        private val instNm: String? = null
        private val ncsCdLst: String? = null
        private val ncsCdNmLst: String? = null
        private val hireTypeLst: String? = null
        private val hireTypeNmLst: String? = null
        private val workRgnLst: String? = null
        private val workRgnNmLst: String? = null
        private val recrutSe: String? = null
        private val recrutSeNm: String? = null
        private val prefCondCn: String? = null
        private val recrutNope: Int? = null
        private val pbancBgngYmd: String? = null
        private val pbancEndYmd: String? = null
        private val recrutPbancTtl: String? = null
        private val srcUrl: String? = null
        private val replmprYn: String? = null
        private val aplyQlfcCn: String? = null
        private val disqlfcRsn: String? = null
        private val scrnprcdrMthdExpln: String? = null
        private val prefCn: String? = null
        private val acbgCondLst: String? = null
        private val acbgCondNmLst: String? = null
        private val nonatchRsn: String? = null
        private val ongoingYn: String? = null
        private val decimalDay: Int? = null
    }
}
