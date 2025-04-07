package com.java.NBE4_5_3_7.domain.news.dto.responseDto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class JobsDetailDto {
    var recrutPblntSn: String? = null
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
    var recrutNope: Int = 0
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
    var decimalDay: Int = 0
    var files: List<Files>? = null
    var steps: List<Steps>? = null

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Files {
        var recrutAtchFileNo: Int = 0 // 필드 추가
        var sortNo: Int = 0
        var atchFileNm: String? = null
        var atchFileType: String? = null
        var url: String? = null
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Steps {
        var recrutStepSn: Int = 0
        var recrutPblntSn: Int = 0
        var recrutPbancTtl: String? = null
        var recrutNope: String? = null
        var aplyNope: String? = null
        var cmpttRt: String? = null
        var rsnOcrnYmd: String? = null
        var sortNo: Int = 0
        var minStepSn: Int = 0
        var maxStepSn: Int = 0
    }

}

