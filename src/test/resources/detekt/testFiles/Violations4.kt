package ktlint.testFiles

import org.jetbrains.kotlin.kotlinx.coroutines.GlobalScope

class Violations4 {
    fun globalCoroutine() {
        GlobalScope.launch { }
    }
}
