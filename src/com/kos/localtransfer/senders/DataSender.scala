package com.kos.localtransfer.senders

import java.io._
import java.net.Socket

import com.kos.languages.RString
import com.kos.localtransfer.ConstantModule._
import com.kos.localtransfer.structs.{IDataListener, ServerStateData}
import com.kos.localtransfer.ConstantModule

import scala.io.StdIn
/**
  * Created by Kos on 14.03.2017.
  */
object DataSender {

	def inputSenderData():ServerStateData={
		val data=new ServerStateData
		println(RString().inputIp)
		data.ipAddress = StdIn.readLine()

		println(RString().inputFileName)
		data.fileSavePath = StdIn.readLine()

		data
	}

	def inputSenderText():ServerStateData={
		val data=new ServerStateData
		println(RString().inputIp)
		data.ipAddress = StdIn.readLine()

		println(RString().inputText)
		data.fileSavePath = StdIn.readLine()
		data
	}


	def mainSendData(listener: IDataListener,state:ServerStateData):Unit = {
//		println("Введите IP-адрес получателя:")
//		val ip = StdIn.readLine()
//
//		println("Введите имя файла:")
//		val fileName = StdIn.readLine()

		send(state.fileSavePath, state.ipAddress, state.port,listener)
	}

	def mainSendText(listener: IDataListener,state:ServerStateData):Unit = {
		sendText(state.fileSavePath, state.ipAddress, state.port,listener)
	}


	def main(args: Array[String]): Unit = {
		println(RString().inputFileName)
		val fileName = StdIn.readLine()
		println(RString().inputIp)
		val ip = StdIn.readLine()

		val listener=ConstantModule.defaultDataListener
		sendText(fileName, ipAddress = ip, ConstantModule.DEFAULT_PORT,listener)

	}

	def send(fileName: String, ipAddress: String, port: Int, listener :IDataListener): Unit = {

		val myFile = new File(fileName)
		val fileNameBytes = myFile.getName.getBytes("UTF-8")
		if (fileNameBytes.length > 127) {
			listener.error(2,RString().messageTooLongFileName)
			return
		}

		val socket = new Socket(ipAddress, port)
		try {
			socket.setSoTimeout(DEFAULT_TIMEOUT)
			//		val receiver = new ServerSocket(ConstantModule.DEFAULT_PORT)
			//		val socket = receiver.accept()
			listener.send(0,RString().messageAcceptConnection + socket)

			val in = new BufferedInputStream(new FileInputStream(myFile))
			try {

				val maxValue=myFile.length().toInt
				val buffer = new Array[Byte](16384)
				//	in.read(buffer,0,buffer.length)
				val out = socket.getOutputStream
				try {
					listener.send(1,RString().messageSendFile)

					//		out.write(buffer,0, buffer.length)
					//		out.flush()

					out.write(fileNameBytes.length.toByte +: fileNameBytes)
					out.flush()

					//	out.write(0)
					var count = 0
					var sum=0
					var i=0
					while ( {
						count = in.read(buffer)
						count > 0
					}) {
						out.write(buffer, 0, count)
						out.flush()
						sum+=count
						i+=1
						if (i%16==0)
							listener.progress(sum,maxValue)
					}
				} finally {
					safeClose(out)
				}
			} finally {
				safeClose(in)
			}

		}finally {
			safeClose(socket)
		}
		listener.complete(RString().completeSendingFiles)
	}

	def sendText(text: String, ipAddress: String, port: Int, listener :IDataListener): Unit = {

		val socket = new Socket(ipAddress, port)
		try {
			socket.setSoTimeout(DEFAULT_TIMEOUT)
			//		val receiver = new ServerSocket(ConstantModule.DEFAULT_PORT)
			//		val socket = receiver.accept()
			listener.send(0,RString().messageAcceptConnection + socket)

			val fileNameBytes = text.getBytes("UTF-8")
			val maxValue=fileNameBytes.length
			if (maxValue> 0 && maxValue<65000 ){
				val out = socket.getOutputStream
				try {
					listener.send(1,RString().messageSendText)

					out.write(fileNameBytes)
					out.flush()

				} finally {
					safeClose(out)
				}
			}

		}finally {
			safeClose(socket)
		}
		listener.complete(RString().messageSendingComplete)
	}
}
