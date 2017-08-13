package com.kos.localtransfer.receivers

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.net.{Inet4Address, NetworkInterface, ServerSocket}

import com.kos.languages.RString
import com.kos.localtransfer.ConstantModule.{DEFAULT_TIMEOUT, safeClose}
import com.kos.localtransfer.structs.{IDataListener, ServerStateData}
import com.kos.localtransfer.ConstantModule

import scala.collection.JavaConverters._
import scala.io.StdIn

/**
  * Created by Kos on 14.03.2017.
  */
object DataReceiver {


	def inputReceiveData():ServerStateData={
		val data=new ServerStateData
		println(RString().inputSaveFolder)
		data.fileSavePath=StdIn.readLine()
		data
	}

	def inputReceiveText():ServerStateData={
		val data=new ServerStateData

		data
	}


	def mainReceiveData(listener: IDataListener,state:ServerStateData ):Unit = {
		printIpAddresses
		//println(RString().inputSaveFolder)
		//val savePath = StdIn.readLine()
		receive(state.fileSavePath,state.port,listener)
	}



	def mainReceiveText(listener: IDataListener,state:ServerStateData):Unit = {
		printIpAddresses
		receiveText(state.port,listener)
	}

	def main(args: Array[String]): Unit = {
		printIpAddresses

		println(RString().inputSaveFolder)
		val savePath = StdIn.readLine()

		val listener = ConstantModule.defaultDataListener
		//println(receiveText(ConstantModule.DEFAULT_PORT, listener))
		receive(savePath, ConstantModule.DEFAULT_PORT,listener)

	}

	private def printIpAddresses = {
		for {network ← NetworkInterface.getNetworkInterfaces.asScala
			 host ← network.getInetAddresses.asScala
		} {
			host match {
				case x:Inet4Address ⇒
					//if (x.isReachable(3000))
					System.out.println(x.getHostAddress)
				case _ ⇒
			}


		}
	}

	def receive(savePath: String, port: Int, listener: IDataListener): Unit = {
		listener.send(0, RString().messageWaiFile)

		val receiver = new ServerSocket(port)
		try {
			val socket = receiver.accept()

			try {
				socket.setSoTimeout(DEFAULT_TIMEOUT)
				val is = socket.getInputStream
				try {
					val buffer = new Array[Byte](16384)
					var byteread = -1
					byteread = is.read(buffer, 0, buffer.length)
					if (byteread > 0) {
						val fLength = buffer.head

						if (fLength > 0) {

							val fileName = new String(buffer.slice(1, fLength + 1), "UTF-8")
							val test = new File(savePath, fileName)
							if (test.exists()) {
								listener.error(1, RString().messageFileIsExists)

							} else {
								listener.send(1, s"${RString().fileFolder} ${test.getAbsolutePath}")
								test.createNewFile()

								val out = new BufferedOutputStream(new FileOutputStream(test))
								try {
									var sum = 0
									val readPart = byteread - fLength - 1
									if (readPart > 0) {
										out.write(buffer, fLength + 1, readPart)
										out.flush()

										sum += byteread
										listener.progress(sum, 0)
									}
									var i=0
									while ( {
										byteread = is.read(buffer, 0, buffer.length)
										byteread != -1
									}) {
										out.write(buffer, 0, byteread)
										out.flush()

										sum += byteread
										i+=1
										if (i%16==0)
											listener.progress(sum, 0)
									}


								} finally {
									safeClose(out)
								}

								listener.send(2, RString().messageCompleteFile)
							}
						}
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


	def receiveText(port: Int, listener: IDataListener): String = {
		listener.send(0, RString().messageWaitText)
		var resultText = ""
		val receiver = new ServerSocket(port)
		try {
			val socket = receiver.accept()


			try {


				socket.setSoTimeout(DEFAULT_TIMEOUT)
				val is = socket.getInputStream
				try {
					val buffer = new Array[Byte](16384)
					val result = Array.newBuilder[Byte]

					var byteread = -1
					while ( {
						byteread = is.read(buffer, 0, buffer.length)
						byteread != -1
					}) {
						result ++= buffer.take(byteread)
					}

					resultText = new String(result.result(), "UTF-8")


				} finally {
					safeClose(is)
				}
			} finally {
				safeClose(socket)
			}
		}finally {
			safeClose(receiver)
		}
		listener.complete(resultText)
		resultText
	}
}
