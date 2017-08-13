package com.kos.localtransfer.structs

/**
  * Created by Kos on 14.03.2017.
  */
trait IDataListener {
	def send(state:Int,message:String)
	def error(code:Int,message:String)
	def complete(message:String)
	def progress(value: Int,length:Int)
}
