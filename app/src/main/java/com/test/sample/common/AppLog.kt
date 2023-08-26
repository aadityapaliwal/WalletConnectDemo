package com.test.sample.common

import android.util.Log
import com.test.sample.BuildConfig

object AppLog {
	private const val isEnabled: Boolean = true
	val internalLogs = StringBuilder()

	/**
	 * Send a [.VERBOSE] log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	fun v(tag: String, msg: String): Int {
		internalLog(Log.VERBOSE,tag, msg)
		return if (isEnabled) Log.v(tag, msg) else 0
	}

	/**
	 * Send a [.VERBOSE] log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	fun v(tag: String, msg: String, tr: Throwable?): Int {
		internalLog(Log.VERBOSE,tag, msg)
		return if (isEnabled) Log.v(tag, msg, tr) else 0
	}

	/**
	 * Send a [.DEBUG] log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	fun d(tag: String, msg: String): Int {
		internalLog(Log.DEBUG,tag, msg)
		return if (isEnabled) Log.d(tag, msg) else 0
	}

	/**
	 * Send a [.DEBUG] log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	fun d(tag: String, msg: String, tr: Throwable?): Int {
		internalLog(Log.DEBUG,tag, msg)
		return if (isEnabled) Log.d(tag, msg, tr) else 0
	}

	/**
	 * Send an [.INFO] log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	fun i(tag: String, msg: String): Int {
		internalLog(Log.INFO,tag, msg)
		return if (isEnabled) Log.i(tag, msg) else 0
	}

	/**
	 * Send a [.INFO] log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	fun i(tag: String, msg: String, tr: Throwable?): Int {
		internalLog(Log.INFO,tag, msg)
		return if (isEnabled) Log.i(tag, msg, tr) else 0
	}

	/**
	 * Send a [.WARN] log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	fun w(tag: String, msg: String): Int {
		internalLog(Log.WARN,tag, msg)
		return if (isEnabled) Log.w(tag, msg) else 0
	}

	/**
	 * Send a [.WARN] log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	fun w(tag: String, msg: String, tr: Throwable?): Int {
		internalLog(Log.WARN,tag, msg)
		return if (isEnabled) Log.w(msg, tr) else 0
	}

	/*
	 * Send a {@link #WARN} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param tr An exception to log
	 */
	fun w(tag: String, tr: Throwable?): Int {
		internalLog(Log.WARN,tag, "")
		return if (isEnabled) Log.w(tag, "", tr) else 0
	}

	/**
	 * Send an [.ERROR] log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	fun e(tag: String, msg: String): Int {
		internalLog(Log.ERROR,tag, msg)
		return if (isEnabled) Log.e(tag, msg) else 0
	}

	/**
	 * Send a [.ERROR] log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 * the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	fun e(tag: String, msg: String, tr: Throwable?): Int {
		internalLog(Log.ERROR,tag, msg)
		return if (isEnabled) Log.e(tag, msg, tr) else 0
	}

	private fun  internalLog(priority: Int, tag: String, msg: String) {
		if (isEnabled) {
			val priorityStr = when (priority) {
				Log.VERBOSE -> "V"
				Log.DEBUG -> "D"
				Log.INFO -> "I"
				Log.WARN -> "W"
				Log.ERROR -> "E"
				else -> "D"
			}
			internalLogs.append("$priorityStr/$tag: $msg\n")
		}
	}
}