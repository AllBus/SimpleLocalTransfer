package com.kos.localtransfer

import java.io.Closeable

import com.kos.languages.RString
import com.kos.localtransfer.structs.IDataListener

/**
  * Created by Kos on 14.03.2017.
  */
object ConstantModule {
	val DEFAULT_PORT=8097
	val DEFAULT_TIMEOUT=30000

	def safeClose(out: Closeable): Unit = {
		try {
			if (out != null)
				out.close()
		}catch {
			case _: Throwable ⇒
		}
	}

	def  defaultDataListener :IDataListener =  {
		new IDataListener {
			override def send(state: Int, message: String) = {
				println(RString().listenerSend(state,message))
			}

			override def complete(message: String) = {
				println(RString().listenerComplete(message))
			}

			override def error(code: Int, message: String): Unit = {
				println(RString().listenerError(code,message))
			}

			override def progress(value: Int, length: Int): Unit = {
				println(RString().listenerProgress(value,length))
			}
		}
	}
}
