package com.java.NBE4_5_3_7.domain.news.dto.responseDto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class JobResponseDto {
    var totalCount = 0
    var result: List<Job>? = null
    var errorMessage: String? = null

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Job {
        var recrutPblntSn: Long? = null
        var pblntInstCd: String? = null
        var pbadmsStdInstCd: String? = null
        var instNm: String? = null
        var ncsCdLst: String? = null
        var ncsCdNmLst: String? = null
        var hireTypeLst: String? = null
        var hireTypeNmLst: String? = null
        var workRgnLst: String? = null
        var workRgnNmLst: String? = null
        var recrutSe: String? = null
        var recrutSeNm: String? = null
        var prefCondCn: String? = null
        var recrutNope: Int? = null
        var pbancBgngYmd: String? = null
        var pbancEndYmd: String? = null
        var recrutPbancTtl: String? = null
        var srcUrl: String? = null
        var replmprYn: String? = null
        var aplyQlfcCn: String? = null
        var disqlfcRsn: String? = null
        var scrnprcdrMthdExpln: String? = null
        var prefCn: String? = null
        var acbgCondLst: String? = null
        var acbgCondNmLst: String? = null
        var nonatchRsn: String? = null
        var ongoingYn: String? = null
        var decimalDay: Int? = null
    }

}
