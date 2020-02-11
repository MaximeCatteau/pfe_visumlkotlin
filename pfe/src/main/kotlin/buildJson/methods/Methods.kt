package buildJson.methods

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sun.org.apache.xpath.internal.operations.Bool
import kastree.ast.Node
import kastree.ast.Visitor
import kastree.ast.psi.Parser

/***
 * @author Maxime CATTEAU
 * This class get the methods informations in order to build the JSON object to send
 * The informations are :
 *  - annotations (#TODO)
 *  - position (#TODO)
 *  - javadoc (#TODO)
 *  - method name (Done)
 *  - static statement (Done)
 *  - constructor statement (No need)
 *  - visibility (Done)
 *  - synchronized statement (No need)
 *  - abstract statement (Done)
 *  - final statement (Done)
 *  - parameters (Done)
 *  - type (Done)
 *  - execution (#TODO)
 */

class Methods {
    private val pathName : String
    private val stringifiedClass: String

    private var isStatic : Boolean = false
    private var isAbstract : Boolean = false
    private var isFinal : Boolean = false

    constructor(pathName: String, stringifiedClass: String){
        this.stringifiedClass = stringifiedClass
        this.pathName = pathName
    }

    public fun getMethods() : JsonArray {
        val classFile = Parser.parseFile(stringifiedClass)
        var jsonArray = JsonArray()

        var methods = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Func){
                jsonArray.add(buildOneMethod(v))
            }
        }
        return jsonArray
    }

    public fun buildOneMethod(function : Node.Decl.Func) : JsonObject {
        var jsonObject = JsonObject()

        jsonObject.add("annotations", JsonArray()) // TODO
        jsonObject.addProperty("position", "") // TODO
        jsonObject.addProperty("javadoc", "") // TODO
        jsonObject.addProperty("name", function.name)
        jsonObject.addProperty("visibility", getVisibility(function))
        jsonObject.addProperty("isStatic", isStatic(function))
        jsonObject.addProperty("isConstructor", false) // Constructors are not declared as methods
        jsonObject.addProperty("isSynchronized", false) // Synchronized keyword doesn't exist in kotlin
        jsonObject.addProperty("isFinal", isFinal(function))
        jsonObject.addProperty("isAbstract", isAbstract(function))
        jsonObject.addProperty("type", getReturnType(function))
        jsonObject.add("parameters", getParameters(function))

        return jsonObject
    }

    public fun getVisibility(function : Node.Decl.Func) : String {

        var visibility : String = "public"

        for(i in function.mods){
            val vis = i.toString()
            val index = vis.lastIndexOf("keyword=")+8
            val lastParenthesis = vis.lastIndexOf(')')
            when(vis.substring(index, lastParenthesis)){
                "INTERNAL" -> visibility = "internal"
                "PRIVATE" -> visibility = "private"
                "PROTECTED" -> visibility = "protected"
                "ABSTRACT" -> isAbstract = true
                "FINAL" -> isFinal = true
                "STATIC" -> isStatic = true
                else -> visibility = "public"
            }
        }

        return visibility
    }

    public fun isStatic(function : Node.Decl.Func) : Boolean {
        return this.isStatic
    }

    public fun isAbstract(function: Node.Decl.Func) : Boolean {
        return this.isAbstract
    }

    public fun isFinal(function : Node.Decl.Func) : Boolean {
        return this.isFinal
    }

    public fun getReturnType(function : Node.Decl.Func) : String {
        var returnType : String = ""

        var references = function.type?.ref.toString()

        if(references.contains("Piece(name=")){
            var indexOfParenthesis = references.indexOf("Piece(name=")+11
            returnType = references.substring(indexOfParenthesis, references.indexOf(","))
        }

        return returnType
    }

    public fun getParameters(function: Node.Decl.Func) : JsonArray {
        var jsonArray : JsonArray = JsonArray()

        println(function.params)

        var rawParams = function.params

        for(param in rawParams){
            var jsonObject : JsonObject = JsonObject()
            jsonObject.addProperty("name", param.name)
            jsonObject.addProperty("type", getParamType(param))
            jsonArray.add(jsonObject)
        }

        return jsonArray
    }

    public fun getParamType(param: Node.Decl.Func.Param) : String {
        var finalParamType : String = ""

        var ref = param.type?.ref.toString()

        if(ref.contains("Piece(name=")){
            var indexOfParenthesis = ref.indexOf("Piece(name=")+11
            finalParamType = ref.substring(indexOfParenthesis, ref.indexOf(","))
        }

        return finalParamType
    }
}