package com.java.NBE4_5_3_7.global.customAnnotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CLASS
)
@Retention(
    AnnotationRetention.RUNTIME
)
@PreAuthorize("@subscriptionPlanChecker.hasPremiumAccess()")
annotation class PremiumAccess