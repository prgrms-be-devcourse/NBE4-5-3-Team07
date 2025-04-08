package com.java.NBE4_5_3_7.global.util

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class LockManager(
	private val redissonClient: RedissonClient
) {

	fun getLock(lockKey: String): RLock {
		return redissonClient.getLock(lockKey)
	}

	@Throws(InterruptedException::class)
	fun acquireLock(lock: RLock, waitTime: Long, leaseTime: Long) {
		if (!lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
			throw RuntimeException("잠시 후 다시 시도해주세요.")
		}
	}

	fun releaseLock(lock: RLock) {
		if (lock.isHeldByCurrentThread) {
			lock.unlock()
		}
	}
}
