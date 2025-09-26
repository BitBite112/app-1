import java.util.ArrayList;

public class MedicineList {

    MedicineList(ArrayList<Medicine> medicineList){
    }

    void populateList(Medicine x){
        String name = "defualt";
        String weight = "default";
        String type = "medicine";
        Medicine medicine = new Medicine(name, weight, type);
    }
}
