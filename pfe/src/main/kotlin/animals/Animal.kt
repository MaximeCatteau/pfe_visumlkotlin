package animals

open class Animal(val type: String, val id: Int, val name: String) {

    constructor() : this("foo", 42, "bar") {
        print("Secondary constructor called")
    }

    override fun toString(): String {
        return "[" + id + "] " + name + " the " + type
    }

    protected fun bar(bar1: Int, bar2: String, bar3: Boolean) : String {
        return "bar"
    }

    fun foo() : Int {
        return 42
    }
}