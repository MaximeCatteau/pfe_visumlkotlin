package buildJson.classInfos

import com.google.gson.JsonArray
import com.sun.xml.internal.fastinfoset.util.StringArray
import kastree.ast.Node
import kastree.ast.Node.TypeRef.Simple.Piece
import kastree.ast.Visitor
import kastree.ast.psi.Parser
import org.jetbrains.kotlin.serialization.js.ast.JsAstProtoBuf
import java.util.*

/***
 * @author Maxime CATTEAU
 * This class get the class main informations in order to build the JSON object to send
 * The informations are :
 *  - project name (Done)
 *  - package name (Done)
 *  - class name (Done)
 *  - location (Done)
 *  - superclass (Done)
 *  - interfaces (Done)
 *  - type (Done)
 *  - visibility (Done)
 *  - abstract statement (Done)
 *  - final statement (Done)
 *  - static statement (Done)
 *  MISSING :
 *  - author
 *  - jd
 */
class Infos {
    // Class related
    private val pathName : String
    private val stringifiedClass: String

    // JSON properties
    private var isAbstract: Boolean = false
    private var isStatic: Boolean = false
    private var isFinal: Boolean = false


    constructor(pathName: String, stringifiedClass: String){
        this.stringifiedClass = stringifiedClass
        this.pathName = pathName
    }

    /***
     * This method returns the project name ACCORDING TO the IntelliJ architecture
     */
    public fun getProjectName() : String {
        val indexSrc = this.pathName.indexOf("/src/", 0) // Find the src word
        val beginStr = pathName.substring(0, indexSrc) // get all the str before src word
        val lastSlash = beginStr.lastIndexOf('/') // get the last slash

        return pathName.substring(lastSlash+1, beginStr.length) // get the project name
    }

    public fun getPackageName() : String {
        val indexSrc = this.pathName.indexOf("kotlin/", 0) // Find the kotlin word
        val beginStr = pathName.substring(indexSrc, pathName.length) // get all the str after kotlin word
        val lastSlash = beginStr.lastIndexOf('/') // get the last slash
        val firstSlash = beginStr.substring(0, lastSlash).lastIndexOf('/')

        return beginStr.substring(firstSlash+1, lastSlash) // get the package name
    }

    /***
     * This method returns the class name
     */
    public fun getClassName() : String{
        val classFile = Parser.parseFile(stringifiedClass)
        var className : String = ""
        var classes = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Structured && v.form == Node.Decl.Structured.Form.CLASS){
                classes += v.name
                className += v.name
            }
        }

        return className
    }

    public fun getLocation() : String {
        val indexSrc = this.pathName.indexOf("kotlin/", 0) // Find the kotlin word
        val beginStr = pathName.substring(indexSrc, pathName.length) // get all the str after kotlin word
        val firstSlash = beginStr.indexOf('/') // get the last slash
        val finalStr = beginStr.substring(firstSlash+1, beginStr.length)

        return finalStr.replace('/', '.') // get the package name
    }

    public fun getSuperclass() : String {
        val classFile = Parser.parseFile(stringifiedClass)
        var superclassStr = ""

        if(getType() == "interface" || getType() == "enum"){
            return ""
        }

        var superclass = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Structured.Parent.CallConstructor && v.type.pieces.isNotEmpty()){
                superclassStr = v.type.pieces.get(0).name
            }
        }
        return superclassStr
    }

    public fun getInterfaces() : JsonArray {
        val classFile = Parser.parseFile(stringifiedClass)

        var interfacesList: MutableList<String> = mutableListOf()

        var pieces: List<Piece> = arrayListOf()

        if(getType() == "interface" || getType() == "enum"){
            return JsonArray()
        }

        var interfaces = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Structured.Parent.Type && v.type.pieces.isNotEmpty()) {
                pieces = v.type.pieces
            }
        }

        for (i in pieces) {
            interfacesList.add(i.name)
        }

        var array : JsonArray = JsonArray()

        for(i in interfacesList){
            array.add(i)
        }

        return array
    }

    public fun getVisibility() : String {
        val classFile = Parser.parseFile(stringifiedClass)

        var visibility = "public"

        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Structured && v.mods.isNotEmpty()){
                for(i in v.mods){
                    val vis = i.toString()
                    val index = vis.lastIndexOf("keyword=")+8
                    val lastParenthesis = vis.lastIndexOf(')')
                    when(vis.substring(index, lastParenthesis)){
                        "INTERNAL" -> visibility = "internal"
                        "PRIVATE" -> visibility = "private"
                        "ABSTRACT" -> isAbstract = true
                        "FINAL" -> isFinal = true
                        "STATIC" -> isStatic = true
                        else -> visibility = "public"
                    }
                }
            }
        }

        return visibility
    }

    public fun getType() : String {
        val classFile = Parser.parseFile(stringifiedClass)

        var type = ""

        var exploration = emptyList<String?>()
        Visitor.visit(classFile) { v, _ ->
            if (v is Node.Decl.Structured && v.form.name.isNotEmpty()){
                when(v.form.name){
                    "CLASS" -> type = "class"
                    "INTERFACE" -> type = "interface"
                    "ENUM" -> type = "enum"
                }
            }
        }

        return type
    }

    public fun isAbstract() : Boolean {
        return this.isAbstract
    }

    public fun isStatic() : Boolean {
        return this.isStatic
    }

    public fun isFinal() : Boolean {
        return this.isFinal
    }
}
