package ktlint.testFiles

import java.util.concurrent.*

class ManyViolations {
  fun test(): String{
      val v = 15;
      return "${v} ${v.toString()}"
  }

    fun bla(){
        println("Hello")
    }
}
