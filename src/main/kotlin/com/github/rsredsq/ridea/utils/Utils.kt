package com.github.rsredsq.ridea.utils

import java.nio.ByteBuffer

fun String.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(toByteArray())

fun String.withNewLine() = this + "\n"