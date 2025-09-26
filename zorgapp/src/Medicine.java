public class Medicine {
    String name;
    String weight;
    String type;

    Medicine(String name, String weight, String type) {
        this.name = name;
        this.weight = weight;
        this.type = type;
    }

    String getName() {
        return name;
    }

    String getWeight() {
        return weight;
    }

    String getType() {return type;}
}