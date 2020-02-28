import buildJson.MyJsonObject
import kastree.ast.Node
import kastree.ast.Visitor
import kastree.ast.psi.Parser
import java.io.File


fun main(){
    var projectName: String = ""
    var packageName: String = ""
    var className: String = ""

    //var pathname = "C:/Users/maxim/IdeaProjects/pfe/src/main/kotlin/animals/AbstractAnimal.kt";
    //var pathname = "C:/Users/maxim/IdeaProjects/pfe/src/main/kotlin/animals/Animal.kt";
    var pathname = "C:/Users/maxim/pfe_visumlkotlin/pfe/src/main/kotlin/animals/Cat.kt";
    //var pathname = "C:/Users/maxim/IdeaProjects/pfe/src/main/kotlin/animals/AnimalInterface.kt";

    val animalClass = File(pathname)

    var stringifiedClass : String = ""
    animalClass.forEachLine {
        stringifiedClass += it + "\n"
    }

    val classFile = Parser.parseFile(stringifiedClass)

    // Construction du json
    val myJsonObject: MyJsonObject =
        MyJsonObject(pathname, stringifiedClass)

    println("### JSON : " + myJsonObject.buildJson())


}