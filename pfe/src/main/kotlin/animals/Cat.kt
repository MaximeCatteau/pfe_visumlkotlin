package animals

open class Cat(type: String, id: Int, name: String, val weight: Double): Animal(type, id, name), AnimalInterface {
     fun sizeTimesTwo() : Double {
        return this.weight * 2
    }

    override fun getAnimalType() : String{
        return "Cat"
    }
}