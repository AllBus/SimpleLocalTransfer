package com.kos.localtransfer

import com.kos.languages.RString
import com.kos.localtransfer.receivers.DataReceiver
import com.kos.localtransfer.senders.DataSender
import com.kos.localtransfer.structs.{IDataListener, ServerStateData}

import scala.annotation.tailrec
import scala.io.StdIn

/**
  * Created by Kos on 17.03.2017.
  */
object Main {
	def main(args: Array[String]): Unit = {

		var stateData=new ServerStateData()
		println(RString().selectMode)
		val modes=RString().modes

		@tailrec
		def loop(previous:Int) {

			println()
			modes.foreach(println)

			val select = StdIn.readLine().toLowerCase
			println()

			val index = modes.indexWhere(x ⇒ x.toLowerCase.contains(select))

			if (index >= 0) {
				val listener = ConstantModule.defaultDataListener

				(if (index==5)	previous else index) match {
					case 0 ⇒ return
					case 1 ⇒
						if (index!=5)
							stateData=DataSender.inputSenderData()
						DataSender.mainSendData(listener,stateData)
					case 2 ⇒
						if (index!=5)
							stateData=DataSender.inputSenderText()
						DataSender.mainSendText(listener,stateData)
					case 3 ⇒
						if (index!=5)
							stateData=DataReceiver.inputReceiveData()
						DataReceiver.mainReceiveData(listener,stateData)
					case 4 ⇒
						if (index!=5)
							stateData=DataReceiver.inputReceiveText()
						DataReceiver.mainReceiveText(listener,stateData)
					case 5 ⇒
					case _ ⇒
				}
				loop(index)
			}
		}
		loop(0)
	}
}
