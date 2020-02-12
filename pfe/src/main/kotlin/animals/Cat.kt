package animals

open class Cat(type: String, id: Int, name: String, val weight: Double): Animal(type, id, name), AnimalInterface {

    private val lastName : String = ""
    val height : Double = 0.0

     fun sizeTimesTwo() : Double {
        return this.weight * 2
    }

    override fun getAnimalType() : String{
        return "Cat"
    }
}