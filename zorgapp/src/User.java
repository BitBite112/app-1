import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

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

    static void populatePatientList(User user){
        int patientID = 0;
        String patientFirstName = null;
        String patientLastName = null;
        LocalDate patientBirthDate = null;
        try{
            String absolutePathString= "C:/zorgAppData/Patienten/";
            File f = new File(absolutePathString + "patientenLijst.txt");
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                patientID = Integer.parseInt(data[0]);
                patientFirstName = data[1];
                patientLastName = data[2];
                patientBirthDate = LocalDate.parse(data[3]);
                Patient patient = new Patient(patientID, patientFirstName, patientLastName, patientBirthDate);
                user.patientList.add(patient);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static String getPassword(String userID){
        String absolutePath = "C:/zorgAppData/";
        String password = null;
        try{
            File f = new File(absolutePath + "Users/Passwords.txt");
            Scanner myReader = new Scanner(f);
            while(myReader.hasNextLine()){
                String[] dataArray = myReader.nextLine().split(",");
                if(dataArray[0].equals(userID)){
                    password = dataArray[1];
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return password;
    }

    static boolean checkPassword(String userID, String givenPassword){
        String password = getPassword(userID);
        if(password.equals(givenPassword)){
            return true;
        } else {
            return false;
        }
    }
}