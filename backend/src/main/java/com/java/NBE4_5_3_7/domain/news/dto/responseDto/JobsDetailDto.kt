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

    @JsonIgnoreProperties(ignoreUnknown = true) // 여기에도 추가
    class Files {
        private val recrutAtchFileNo = 0 // 필드 추가
        private val sortNo = 0
        private val atchFileNm: String? = null
        private val atchFileType: String? = null
        private val url: String? = null
    }

    @JsonIgnoreProperties(ignoreUnknown = true) // 여기에도 추가
    class Steps {
        private val recrutStepSn = 0
        private val recrutPblntSn = 0
        private val recrutPbancTtl: String? = null
        private val recrutNope: String? = null
        private val aplyNope: String? = null
        private val cmpttRt: String? = null
        private val rsnOcrnYmd: String? = null
        private val sortNo = 0
        private val minStepSn = 0
        private val maxStepSn = 0
    }
}

