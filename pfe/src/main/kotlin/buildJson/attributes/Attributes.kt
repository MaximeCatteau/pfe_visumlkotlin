package buildJson.attributes

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sun.org.apache.xpath.internal.operations.Bool
import kastree.ast.Node
import kastree.ast.Visitor
import kastree.ast.psi.Parser

class Attributes {

    private var pathname : String = ""
    private var stringifiedClass : String = ""

    constructor(pathname : String, stringifiedClass : String){
        this.pathname = pathname
        this.stringifiedClass = stringifiedClass
    }

    public fun buildAttribute(v : Node.Decl.Property.Var) : JsonObject {
        var jsonObject : JsonObject = JsonObject()

        jsonObject.addProperty("javadoc", "")
        jsonObject.addProperty("visibility", "")
        jsonObject.addProperty("type", getType(v))
        jsonObject.addProperty("name", v.name)
        jsonObject.addProperty("isStatic", isStatic(v))
        jsonObject.addProperty("isVolatile", false)
        jsonObject.addProperty("isFinal", isFinal(v))
        jsonObject.addProperty("value", "")

        return jsonObject
    }

    public fun isFinal(v : Node.Decl.Property.Var) : Boolean {
        return false
    }

    public fun isStatic(v : Node.Decl.Property.Var) : Boolean {
        return false
    }

    public fun getType(v : Node.Decl.Property.Var) : String {
        var type : String = ""

        return type
    }

    public fun getAttributesArray() : JsonArray {

        val classFile = Parser.parseFile(stringifiedClass)

        var jsonArray : JsonArray = JsonArray()

        var superclass = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Property.Var){
                println("### " + v)
                jsonArray.add(buildAttribute(v))
            }
        }

        return jsonArray
    }

}