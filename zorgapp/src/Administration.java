import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * class Administration represents the core of the application by showing
 * the main menu, from where all other functionality is accessible, either
 * directly or via sub-menus.
 * An Administration instance needs a User as input, which is passed via the
 * constructor to the data member 'currentUser'.
 * The patient data is available via the data member currentPatient.
 */
class Administration {
    static final int STOP = 0;
    static final int VIEW = 1;
    static final int VIEWMEDICINE = 2;
    static final int VIEWAPPOINTMENTS = 3;
    static final int ADDPATIENT = 4;
    static final int ADDMEDICINE = 5;
    static final int ADDAPPOINTMENT = 6;

    Patient currentPatient;            // The currently selected patient
    User currentUser;               // the current user of the program.

    /**
     * Constructor
     */
    Administration(User user) {
        readPatientList();
        Scanner myObj = new Scanner(System.in);
        System.out.println("Please enter your patient's id: ");
        int patientId = myObj.nextInt();
        currentUser = user;
        currentPatient = readPatientInfo(patientId);

        System.out.format("Current user: [%d] %s\n", user.getUserID(), user.getUserName());
    }

    void menu() {
        int patientId = incrementLatestPatientId();
        readMedicineInfo(currentPatient, currentUser);
        readAppointmentInfo(currentPatient);

        var scanner = new Scanner(System.in);  // User input via this scanner.
        boolean nextCycle = true;
        while (nextCycle) {
            System.out.format("%s\n", "=".repeat(80));
            System.out.format("Current patient: %s\n", currentPatient.fullName());

            /*
             Print menu on screen
            */
            System.out.format("%d:  STOP\n", STOP);
            System.out.format("%d:  View patient data\n", VIEW);
            System.out.format("%d:  View medicines\n", VIEWMEDICINE);
            System.out.format("%d:  View appointments\n", VIEWAPPOINTMENTS);
            System.out.printf("%d:  Add patient\n", ADDPATIENT);
            System.out.printf("%d:  Add medicine\n", ADDMEDICINE);
            System.out.printf("%d:  Add appointment\n", ADDAPPOINTMENT);
            System.out.println("THE MEDICINESLIST: " + currentPatient.medicineList);
            System.out.println("THE APPOINTMENTSLIST: " + currentPatient.appointmentList);

            System.out.print("enter #choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case STOP: // interrupt the loop
                    nextCycle = false;
                    break;

                case VIEW:
                    currentPatient.viewData();
                    break;

                case VIEWMEDICINE:
                    readMedicineInfo(currentPatient, currentUser);
                    break;

                case VIEWAPPOINTMENTS:
                    readAppointmentInfo(currentPatient);
                    break;

                case ADDPATIENT:
                    addPatient(patientId);
                    break;

                case ADDMEDICINE:
                    addMedicine(currentPatient.id);
                    break;

                case ADDAPPOINTMENT:
                    addAppointment(currentPatient.id);
                    break;

                default:
                    System.out.println("Please enter a *valid* digit");
                    break;
            }
        }
    }

    static void populateMedicineList() {}

    static void populateAppointmentList() {}

    static Patient readPatientInfo(int patientId) {
        String absolutePath = "C:/zorgAppData/Patienten/" + patientId + "/";
        try {
            File f = new File(absolutePath + "informatie.txt");
            Scanner myReader = new Scanner(f);
            ArrayList<String> ListArray = new ArrayList<String>();
            while (myReader.hasNextLine()) {
                ListArray.add(myReader.nextLine());
            }

            String[] myArray = ListArray.get(0).split(",");

            int id = parseInt(myArray[0]);
            String firstName = myArray[1];
            String surName = myArray[2];
            LocalDate dateOfBirth = LocalDate.parse(myArray[3]);
            int weight = parseInt(myArray[4]);
            int height = parseInt(myArray[5]);
            char sex = myArray[6].charAt(0);

            return new Patient(id, firstName, surName, dateOfBirth, weight, height, sex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void readPatientList() {
        String absolutePath = "C:/zorgAppData/Patienten/patientenLijst.txt";
        try {
            File f = new File(absolutePath);
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                String[] myArray = line.split(",");
                System.out.println("|| Patient ID:      " + myArray[0]);
                System.out.println("|| First Name:      " + myArray[1]);
                System.out.println("|| Last Name:       " + myArray[2]);
                System.out.println("|| Date of Birth:   " + myArray[3]);
                System.out.format("%s", "-".repeat(10));
                System.out.println("");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void readMedicineInfo(Patient patient, User user) {
        String absolutePath = "C:/zorgAppData/Patienten/" + patient.id + "/";
        try {
            File f = new File(absolutePath + "medicijnen.txt");
            Scanner myReader = new Scanner(f);
            ArrayList<String> ListArray = new ArrayList<String>();
            while (myReader.hasNextLine()) {
                String[] myArray = myReader.nextLine().split(",");
                String name = myArray[0];
                String weight = myArray[1];
                String type = myArray[2];
                Medicine medicine = new Medicine(name, weight, type);
                patient.addMedicine(medicine);
            }
            //Medicine list
            System.out.println(patient.getMedicineList(user.beroep));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void readAppointmentInfo(Patient patient) {
        String absolutePath = "C:/zorgAppData/Patienten/" + patient.id + "/";
        File f = new File(absolutePath + "afspraken.txt");
        ArrayList<String> lines = new ArrayList<String>();
        try {
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String[] myArray = myReader.nextLine().split(",");
                LocalDate date = LocalDate.parse(myArray[0]);
                String time = myArray[1];
                String description = myArray[2];
                Appointment appointment = new Appointment(date, time, description);
                patient.addAppointment(appointment);
            }
            patient.getAppointmentList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int incrementLatestPatientId() {
        String absolutePath = "C:/zorgAppData/Patienten/";
        String stringId = null;
        File f = new File(absolutePath + "patientenLijst.txt");
        try {
            Scanner scanner = new Scanner(f);
            String line = scanner.nextLine();
            while (scanner.hasNextLine()) {
                if (line.isEmpty()) {
                    System.out.println("CONTINUING");
                    continue;
                }
                String[] myArray = scanner.nextLine().split(",");
                stringId = myArray[0];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int patientId = parseInt(stringId);
        return patientId + 1;
    }

    static void addPatient(int newPatientId) {
        String name;
        String surName;
        String dateOfBirth;
        String weight;
        String height;
        String sex;

        String absolutePath = "C:/zorgAppData/Patienten/";
        try {
            List<String> lines = Files.readAllLines(Paths.get(absolutePath
                    + "patientenlijst.txt"));

            System.out.println("LINES: " + lines);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter first name: ");
            name = scanner.nextLine();
            System.out.println("Enter sur name: ");
            surName = scanner.nextLine();
            System.out.println("Enter date of birth: ");
            dateOfBirth = scanner.nextLine();
            System.out.println("Enter weight: ");
            weight = scanner.nextLine();
            System.out.println("Enter height: ");
            height = scanner.nextLine();
            System.out.println("Enter sex: ");
            sex = scanner.nextLine();
            String textToAdd = newPatientId + "," + name + "," +
                    surName + "," + dateOfBirth + "," + weight +
                    "," + height + "," + sex;
            String textToAdd2 = newPatientId + "," + name + "," +
                    surName + "," + dateOfBirth;
            System.out.println("TEXT TO ADD2: " + textToAdd2);

            try {
                File myObj6 = new File(absolutePath + "patientenlijst.txt");
                FileWriter myWriter = new FileWriter(absolutePath + "patientenLijst.txt", true);
                Scanner scanner2 = new Scanner(myObj6);
                if (scanner2.equals("")) {
                    myWriter.write(textToAdd);
                } else {
                    myWriter.write("\n" + textToAdd2);
                }
                File myObj = new File(absolutePath + "/" + newPatientId);
                File myObj2 = new File(absolutePath + "/" + newPatientId + "/"
                        + "Gewicht & lengte.txt");
                File myObj3 = new File(absolutePath + "/" + newPatientId + "/"
                        + "informatie.txt");
                File myObj4 = new File(absolutePath + "/" + newPatientId + "/"
                        + "Medicijnen.txt");
                File myObj5 = new File(absolutePath + "/" + newPatientId + "/"
                        + "afspraken.txt");
                myObj.mkdir();
                myObj2.createNewFile();
                myObj3.createNewFile();
                myObj4.createNewFile();
                myObj5.createNewFile();

                myWriter.close();
                FileWriter myWriter2 = new FileWriter(absolutePath +
                        newPatientId + "/" + "informatie.txt", true);
                myWriter2.write(textToAdd);
                myWriter2.close();
                System.out.println("THE PATH:");
                System.out.println(absolutePath + newPatientId + "/" + "informatie.txt");


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void addMedicine(int patientId) {
        String name;
        String weight;
        String type;
        String absolutePath = "C:/zorgAppData/Patienten/";
        try {
            FileWriter fileWriter = new FileWriter(absolutePath +
                    patientId + "/" + "Medicijnen.txt", true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter medicine name: ");
            name = scanner.nextLine();
            System.out.println("Enter medicine weight: ");
            weight = scanner.nextLine();
            System.out.println("Enter medicine type: ");
            type = scanner.nextLine();

            String stringToAdd = name + "," + weight + "," + type;
            fileWriter.write(stringToAdd);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void addAppointment(int patientId){
        String stringDate;
        String appointmentTime;
        String appointmentDescription;
        String absolutePath = "C:/zorgAppData/Patienten/";

        try {
            FileWriter fileWriter = new FileWriter(absolutePath + patientId +
                    "/" + "afspraken.txt");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter appointment date: ");
            stringDate = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate appointmentDate = LocalDate.parse(stringDate, formatter);
            System.out.println("Enter appointment time: ");
            appointmentTime = scanner.nextLine();
            System.out.println("Enter appointment description: ");
            appointmentDescription = scanner.nextLine();

            Appointment appointment = new Appointment(appointmentDate, appointmentTime, appointmentDescription);

            String stringToAdd = appointmentDate + "," + appointmentTime + "," + appointmentDescription;
            fileWriter.write(stringToAdd);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updatePatient(Patient currentPatient) {

    }

    static void updateMedicine() {
    }

    static void updateAppointment() {
    }
}