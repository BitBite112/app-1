import java.util.ArrayList;

class User {
    String userName;
    int userID;
    String beroep;
    ArrayList<Patient> patientList = new ArrayList<Patient>();

    public User(int id, String name, String beroep) {
        this.userID = id;
        this.userName = name;
        this.beroep = beroep;
    }

    String getUserName() {
        return userName;
    }

    int getUserID() {
        return userID;
    }

    static void populatePatientList(){

    }
}
