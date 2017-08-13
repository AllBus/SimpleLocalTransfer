package com.kos.localtransfer.receivers

import java.io.ByteArrayInputStream
import java.net.{NetworkInterface, ServerSocket}
import javax.sound.sampled._

import com.kos.languages.RString
import com.kos.localtransfer.ConstantModule.{DEFAULT_TIMEOUT, safeClose}
import com.kos.localtransfer.ConstantModule
import com.kos.localtransfer.structs.IDataListener

import scala.collection.JavaConverters._

/**
  * Created by Kos on 15.03.2017.
  */


object AudioReceiver {
	val buffer = new Array[Byte](16384)
	val audio = new Array[Byte](16384)

	def main(args: Array[String]): Unit = {
		for {network ← NetworkInterface.getNetworkInterfaces.asScala
			 host ← network.getInetAddresses.asScala
		} {
			System.out.println(host.getHostAddress)
		}

		val listener=ConstantModule.defaultDataListener
		receive( ConstantModule.DEFAULT_PORT,listener)

	}

	def receive( port: Int, listener :IDataListener): Unit = {
		listener.send(0, "Listen audio")

		val receiver = new ServerSocket(port)
		try {
			val socket = receiver.accept()

			try {
				socket.setSoTimeout(DEFAULT_TIMEOUT)
				val is = socket.getInputStream
				try {
					var byteread = -1

					val sampleRate = 8000f
					val sampleSize = 16
					val channel = 1
					val sign = true
					val bigendian = false
					val format = new AudioFormat(sampleRate, sampleSize, channel, sign, bigendian);

					var voiceLine: SourceDataLine = null
					val LineInfo = new DataLine.Info(classOf[SourceDataLine], format)
					if (AudioSystem.isLineSupported(LineInfo)) {
						System.out.println("Line Supported...")
					} else {
						System.out.println("not supported Line...")
					}
					voiceLine = AudioSystem.getLine(LineInfo).asInstanceOf[SourceDataLine]
					voiceLine.open(format)
					voiceLine.start()

					byteread = is.read(buffer, 0, buffer.length)

					//	while(byteread > 0) {
					while (byteread >= 0) {
						if (byteread > 0) {
							val in = new ByteArrayInputStream(buffer)
							val ais = new AudioInputStream(in, format, buffer.length / format.getFrameSize());
							ais.read(audio, 0, byteread)
							voiceLine.write(audio, 0, byteread)
						}
						byteread = is.read(buffer, 0, buffer.length)
					}
				} finally {
					safeClose(is)
				}
			} finally {
				safeClose(socket)
			}
		}finally {
			safeClose(receiver)
		}
		listener.complete(RString().complete)
	}
}
