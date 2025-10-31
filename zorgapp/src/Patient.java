import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

class Patient {
    static final int RETURN = 0;
    static final int SURNAME = 1;
    static final int FIRSTNAME = 2;
    static final int DATEOFBIRTH = 3;

    int id;
    String surname;
    String firstName;
    LocalDate dateOfBirth;
    int weight; //KG
    int height; //CM
    char sex;
    ArrayList<Medicine> medicineList = new ArrayList<Medicine>();
    ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();


    /**
     * Constructor
     */
    Patient(int id, String firstName, String surname, LocalDate dateOfBirth,
            int weight, int height, char sex, ArrayList<Medicine> medicineList,
            ArrayList<Appointment> appointmentList) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.medicineList = medicineList;
    }

    Patient(int id, String firstName, String surname, LocalDate dateOfBirth, int weight, int height, char sex) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.medicineList = medicineList;
    }

    Patient(int id, String firstName, String surname, LocalDate dateOfBirth) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
    }

    String getSurname() {
        return surname;
    }

    String getFirstName() {
        return firstName;
    }

    LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    int getWeight() {
        return weight;
    }

    int getHeight() {
        return height;
    }

    char getSex() {
        return sex;
    }

    double calcBMI() {
        return (weight / (height * height));
    }

    int getAge() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getYear() - dateOfBirth.getYear();
    }

    public void addMedicine(Medicine medicine) {
        medicineList.add(medicine);
    }

    ArrayList<Medicine> getMedicineList(String userBeroep) {
        for(Medicine med : medicineList){
            if(userBeroep.equals("physiotherapeut")  && med.type.equals("pijnstiller")){
                continue;
            } else if (userBeroep.equals("tandarts") && !med.type.equals("verdovingsmiddel")) {
                continue;
            }
            System.out.println(med.name + " " + med.weight + " " + med.type);
        }
        return null;

    }

    public void addAppointment(Appointment appointment){
        appointmentList.add(appointment);
    }

    ArrayList<Appointment> getAppointmentList() {
        for(Appointment ap : appointmentList){
            System.out.format("%s %s\n", ap.appointmentDate, ap.appointmentTime);
            System.out.println(ap.appointmentDescription + "\n----------------");
        }
        return appointmentList;
    }

    /**
     * Display patient data.
     */
    void viewData() {
        System.out.format("===== Patient id=%d ==============================\n", id);
        System.out.format("%-17s %s\n", "Surname:", surname);
        System.out.format("%-17s %s\n", "firstName:", firstName);
        System.out.format("%-17s %d\n", "Age:", getAge());
        System.out.format("%-17s %s %s\n", "Weight: ", weight, " kg");
        System.out.format("%-17s %s %s\n", "Height: ", height, " cm");
        System.out.format("%-17s %s\n", "Sex: ", sex);
    }

    /**
     * Shorthand for a Patient's full name
     */
    String fullName() {
        return String.format("%s %s [%s]", firstName, surname, dateOfBirth.toString());
    }
}
