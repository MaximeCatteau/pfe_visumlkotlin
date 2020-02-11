package buildJson

import buildJson.classInfos.Infos
import buildJson.methods.Methods
import com.google.gson.JsonObject
import java.util.*

class MyJsonObject {
    private val pathName: String
    private val stringifiedClass: String

    constructor(pathName: String, stringifiedClass: String){
        this.pathName = pathName
        this.stringifiedClass = stringifiedClass
    }

    fun buildJson(): JsonObject {
        val finalObject: JsonObject = JsonObject()

        val infos : Infos = Infos(this.pathName, this.stringifiedClass)
        val methods : Methods = Methods(this.pathName, this.stringifiedClass)

        // Class Infos
        finalObject.addProperty("projet", infos.getProjectName())
        finalObject.addProperty("packageName", infos.getPackageName())
        finalObject.addProperty("name", infos.getClassName())
        finalObject.addProperty("location", infos.getLocation())
        finalObject.addProperty("superClass", infos.getSuperclass())
        finalObject.add("interfaces", infos.getInterfaces())
        finalObject.addProperty("visibility", infos.getVisibility())
        finalObject.addProperty("type", infos.getType())
        finalObject.addProperty("isFinal", infos.isFinal())
        finalObject.addProperty("isAbstract", infos.isAbstract())
        finalObject.addProperty("isStatic", infos.isStatic())

        // Methods
        finalObject.add("methods", methods.getMethods())

        return finalObject
    }

}