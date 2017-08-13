package com.kos.languages

class DefaultConstants {
	val messageSendingComplete = "Finished sending"

	val messageSendText = "Sending text"

	val completeSendingFiles = "Finished sending"

	val messageSendFile = "Sending files"

	val messageAcceptConnection = "Accepted connection from: "


	val inputSaveFolder = "Input folder name for save file:"
	val inputText = "Input sending text:"

	val inputFileName = "Input file name:"

	val inputIp = "Input IP-address of receiver:"

	val messageWaitText = "Waiting text"

	val messageCompleteFile = "Complete load file"
	val messageWaiFile = "Waiting file"
	val messageTooLongFileName = "Too long file name"

	val fileFolder = "File path:"

	val messageFileIsExists = "file is exists"





	val selectMode = "Select mode:"

	val modes = Array(

		"0 - Exit",
		"1 - Send file",
		"2 - Send text",
		"3 - Receive file",
		"4 - Receive text",
		"5 - Retry")

	def listenerSend(state: Int, message: String) = s"state $state - $message"
	def listenerComplete(message:String) = "Complete - "+message
	def listenerError(code: Int, message: String) =	s"Error $code - $message"
	def listenerProgress(value: Int, length: Int) = s"progress $value / $length"

	val complete ="Complete"
}
