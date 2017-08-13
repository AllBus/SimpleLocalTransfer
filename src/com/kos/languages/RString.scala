package com.kos.languages

object RString {

	private[this] var constants = new DefaultConstants

	def apply() = constants

	def changeToRu() = constants = new RuConstants
	def changeToEn() = constants = new DefaultConstants
}
