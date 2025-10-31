import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
    static final int VIEWPATIENTINFO = 1;
    static final int ADDPATIENTINFO = 2;
    static final int UPDATEPATIENTINFO = 3;
    static final int VIEW = 1;
    static final int VIEWMEDICINE = 2;
    static final int VIEWAPPOINTMENTS = 3;
    static final int ADDPATIENT = 1;
    static final int ADDMEDICINE = 2;
    static final int ADDAPPOINTMENT = 3;
    static final int UPDATEPATIENT = 1;
    static final int UPDATEMEDICINE = 2;
    static final int UPDATEAPPOINTMENT = 3;

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
        System.out.println();
        currentUser = user;
        currentPatient = readPatientInfo(patientId);
        User.populatePatientList(currentUser);
        populateMedicineList(currentPatient);
        populateAppointmentList(currentPatient);
        System.out.println("PATIENT LIST OBJECT: " + user.patientList);

        System.out.format("Current user: [%d] %s\n", user.getUserID(), user.getUserName());
    }

    void menu() {
        int patientId = incrementLatestPatientId();

        var scanner = new Scanner(System.in);  // User input via this scanner.
        boolean nextCycle = true;
        while (nextCycle) {
            System.out.format("%s\n", "=".repeat(80));
            System.out.format("Current patient: %s\n", currentPatient.fullName());
            System.out.printf("%d:  STOP\n", STOP);
            System.out.printf("%d:  View information options\n", VIEWPATIENTINFO);
            System.out.printf("%d:  View Patient adding options\n", VIEWMEDICINE);
            System.out.printf("%d   View editing options\n", VIEWAPPOINTMENTS);
            System.out.print("enter #choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case STOP:
                    nextCycle = false;
                    break;
                case VIEWPATIENTINFO:
                    System.out.printf("%d:  View Patient info\n", VIEW);
                    System.out.printf("%d:  View Patient medication\n", VIEWMEDICINE);
                    System.out.printf("%d:  View Patient appointments\n", VIEWAPPOINTMENTS);
                    int choice2 = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice2) {
                        case VIEW:
                            clearConsole();
                            currentPatient.viewData();
                            break;

                        case VIEWMEDICINE:
                            readMedicineInfo(currentPatient, currentUser);
                            break;

                        case VIEWAPPOINTMENTS:
                            readAppointmentInfo(currentPatient);
                            break;
                    }
                    break;
                case ADDPATIENTINFO:
                    if(currentUser.beroep == "apotheker"){
                        System.out.println("You are not authorised to make any changes.");
                        break;
                    }
                    System.out.printf("%d:  Add patient\n", ADDPATIENT);
                    System.out.printf("%d:  Add medicine\n", ADDMEDICINE);
                    System.out.printf("%d:  Add appointment\n", ADDAPPOINTMENT);
                    int choice3 = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice3) {
                        case ADDPATIENT:
                            addPatient(patientId);
                            break;

                        case ADDMEDICINE:
                            addMedicine(currentPatient.id);
                            break;

                        case ADDAPPOINTMENT:
                            addAppointment(currentPatient.id);
                            break;
                    }
                    break;

                case UPDATEPATIENTINFO:
                    if(currentUser.beroep == "apotheker"){
                        System.out.println("You are not authorised to make any changes.");
                    }
                    System.out.printf("%d:  Update patient info\n", UPDATEPATIENT);
                    System.out.printf("%d:  Update medicine\n", UPDATEMEDICINE);
                    System.out.printf("%d:  Update appointment\n", UPDATEAPPOINTMENT);
                    int choice4 = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice4) {
                        case UPDATEPATIENT:
                            updatePatient(currentPatient, currentPatient.medicineList);
                            break;

                        case UPDATEMEDICINE:
                            updateMedicine(currentPatient, currentPatient.medicineList);
                            break;

                        case UPDATEAPPOINTMENT:
                            updateAppointment(currentPatient, currentPatient.appointmentList);
                            break;
                    }
                    break;
            }
        }
    }

    static void populateMedicineList(Patient patient) {
        String absolutePath = "C:/zorgAppData/Patienten/" + patient.id + "/";
        try {
            File f = new File(absolutePath + "medicijnen.txt");
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String[] myArray = myReader.nextLine().split(",");
                String name = myArray[0];
                String weight = myArray[1];
                String type = myArray[2];
                Medicine medicine = new Medicine(name, weight, type);
                patient.addMedicine(medicine);
            }
            //Medicine list
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void populateAppointmentList(Patient patient) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        patient.getMedicineList(user.beroep);
    }

    static void readAppointmentInfo(Patient patient) {
        System.out.println("1:  View appointments");
        System.out.println("2:  Sort appointment list");
        Scanner myObj = new Scanner(System.in);
        while (true) {
            int choice = myObj.nextInt();
            System.out.println();
            if (choice == 1) {
                patient.getAppointmentList();
                break;
            } else if (choice == 2) {
                sortAppointments(patient);
                break;
            } else {
                System.out.println("Invalid choice");
            }
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
            System.out.println("Day:  ");
            String day = scanner.nextLine();
            System.out.println("Month:  ");
            String month = scanner.nextLine();
            System.out.println("Year:  ");
            String year = scanner.nextLine();
            //LocalDate must be in format of YYYY-MM-DD, but must be shown to the user in DD-MM-YYYY.
            dateOfBirth = year+"-"+month+"-"+day;
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

    static void addAppointment(int patientId) {
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

    static void updatePatient(Patient currentPatient, ArrayList<Medicine> medicines) {
        String absolutePath = "C:/zorgAppData/Patienten/";
        String dataToBeChanged = null;
        File f = new File(absolutePath + currentPatient.id + "/informatie.txt");
        File f2 = new File(absolutePath + "patientenLijst.txt");

        while(true){
            System.out.println("Please choose what you want to update: ");
            System.out.println("0: Stop");
            System.out.println("1:  First Name");
            System.out.println("2:  Last Name");
            System.out.println("3:  Date Of Birth");
            System.out.println("4:  Weight");
            System.out.println("5:  Height");
            System.out.println("6:  Gender");
            Scanner myObj = new Scanner(System.in);
            int choice = myObj.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    System.out.println("Enter First Name: ");
                    dataToBeChanged = myObj.nextLine();
                    currentPatient.firstName = dataToBeChanged;
                    break;
                case 2:
                    System.out.println("Enter Last Name: ");
                    dataToBeChanged = myObj.nextLine();
                    currentPatient.surname = dataToBeChanged;
                    break;
                case 3:
                    System.out.println("Enter Date Of Birth: ");
                    System.out.println("Enter day:  ");
                    String day = myObj.nextLine();
                    System.out.println("Enter month:  ");
                    String month = myObj.nextLine();
                    System.out.println("Enter year:  ");
                    String year = myObj.nextLine();
                    dataToBeChanged = year+"-"+month+"-"+day;
                    currentPatient.dateOfBirth = LocalDate.parse(dataToBeChanged);
                    break;
                case 4:
                    System.out.println("Enter Weight: ");
                    dataToBeChanged = myObj.nextLine();
                    currentPatient.weight = Integer.parseInt(dataToBeChanged);
                    break;
                case 5:
                    System.out.println("Enter Height: ");
                    dataToBeChanged = myObj.nextLine();
                    currentPatient.height = Integer.parseInt(dataToBeChanged);
                    break;
                case 6:
                    while (true) {
                        System.out.println("Enter m for male or f for female: ");
                        dataToBeChanged = myObj.nextLine();
                        dataToBeChanged = dataToBeChanged.toLowerCase();
                        if (dataToBeChanged == "m" || dataToBeChanged == "f") {
                            break;
                        } else {
                            System.out.println("Invalid input");
                        }
                    }
                    currentPatient.sex = dataToBeChanged.charAt(0);
                    break;
            }
            if(choice == 0){
                break;
            }
        }
        
        String dataToBeAdded = currentPatient.id + "," + currentPatient.firstName + "," +
                currentPatient.surname + "," + currentPatient.dateOfBirth + "," +
                currentPatient.weight + "," + currentPatient.height + "," + currentPatient.sex;
        String dataToBeAdded2 = currentPatient.id + "," +  currentPatient.firstName + "," +
                currentPatient.surname + "," + currentPatient.dateOfBirth;
        try {
            List<String> lines = Files.readAllLines(Paths.get(absolutePath + "patientenLijst.txt"));
            List<String> lines2 = Files.readAllLines(Paths.get(absolutePath + currentPatient.id + "/Gewicht & lengte.txt"));
            FileWriter myWriter = new FileWriter(absolutePath + currentPatient.id + "/informatie.txt");
            FileWriter myWriter2 = new FileWriter(absolutePath + "patientenLijst.txt");
            FileWriter myWriter3 = new FileWriter(absolutePath + currentPatient.id + "/Gewicht & lengte.txt");

            Scanner scanner2 = new Scanner(f2);
            lines.set(currentPatient.id, dataToBeAdded2);
            for (int i = 0; i < lines.size(); i++) {
                if (i == lines.size() - 1) {
                    myWriter2.write(lines.get(i));
                } else {
                    myWriter2.write(lines.get(i) + "\n");
                }
            }
            myWriter2.close();
            myWriter.write(dataToBeAdded);
            myWriter.close();
            String dataToBeAdded3 = lines2.get(0) + "," + currentPatient.weight + "\n" +
                    lines2.get(1) + "," + currentPatient.height;
            myWriter3.write(dataToBeAdded3);
            myWriter3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updateMedicine(Patient patient, ArrayList<Medicine> medicines) {
        int counter = 0;
        System.out.println("Choose what entry to edit: ");
        for (Medicine medicine : medicines) {
            System.out.println(counter + " - " + medicine.getName() + ", " +
                    medicine.getWeight() + ", " + medicine.getType());
            counter++;
        }
        Scanner myObj = new Scanner(System.in);
        int entryChoice = myObj.nextInt();
        myObj.nextLine();
        System.out.println("Choose to edit name, weight or type: ");
        System.out.println("Name: 1\nWeight: 2\nType: 3");
        String choice = myObj.nextLine();

        if (choice.equals("1")) {
            System.out.println("Enter the medicines name: ");
            String newEntry = myObj.nextLine();
            medicines.get(entryChoice).name = newEntry;
        } else if (choice.equals("2")) {
            System.out.println("Enter the medicines weight: ");
            String newEntry = myObj.nextLine();
            medicines.get(entryChoice).weight = newEntry;
        } else if (choice.equals("3")) {
            System.out.println("Enter the medicines type: ");
            String newEntry = myObj.nextLine();
            medicines.get(entryChoice).type = newEntry;
        } else {
            System.out.println("NOTHING!");
            System.out.println(choice);
        }
        //Adding to file
        String medicineToAdd = medicines.get(entryChoice).name + "," +
                medicines.get(entryChoice).weight + "," +
                medicines.get(entryChoice).type;
        System.out.println("MEDCINETOADD:" + medicineToAdd);
        String absolutePath = "C:/zorgAppData/Patienten/";
        try {
            File f = new File(absolutePath + patient.id + "/Medicijnen.txt");
            List<String> lines = Files.readAllLines(f.toPath());
            lines.set(entryChoice, medicineToAdd);
            System.out.println("LINES:");
            System.out.println(lines);

            FileWriter myWriter = new FileWriter(absolutePath + patient.id + "/Medicijnen.txt");
            myWriter.write(lines.get(0));
            for (int i = 1; i < lines.size(); i++) {
                myWriter.write("\n" + lines.get(i));
            }
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updateAppointment(Patient patient, ArrayList<Appointment> appointments) {
        int counter = 0;
        System.out.println("Choose what entry to edit: ");
        for (Appointment appointment : appointments) {
            System.out.println(counter + " - " + appointment.appointmentDate + " " +
                    appointment.appointmentTime + "\n" + appointment.appointmentDescription);
            counter++;
        }
        Scanner myObj = new Scanner(System.in);
        int entryChoice = myObj.nextInt();
        System.out.println();
        System.out.println("Choose to edit the date, time or description: ");
        System.out.println("Date: 1\nTime: 2\nDescription: 3");
        String optionChoice = myObj.nextLine();

        List<String> descriptionLines = null;
        String absolutePath = "C:/zorgAppData/Patienten/";
        try {
            File f = new File(absolutePath + patient.id + "/afspraken.txt");
            descriptionLines = Files.readAllLines(f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (optionChoice.equals("1")) {
            System.out.println("Enter the appointment date: ");
            String newEntry = myObj.nextLine();
            appointments.get(entryChoice).appointmentDate = LocalDate.parse(newEntry);
        } else if (optionChoice.equals("2")) {
            System.out.println("Enter the appointment time: ");
            String newEntry = myObj.nextLine();
            appointments.get(entryChoice).appointmentTime = newEntry;
        } else if (optionChoice.equals("3")) {
            String[] descriptionArray = descriptionLines.get(entryChoice).split(",");
            descriptionArray = descriptionArray[2].split(" ");
            System.out.println("THE ARRAY:");

            for (String word : descriptionArray) {
                System.out.print(word + " ");
            }
            System.out.println();
            for (int i = 0; i < descriptionArray.length && i <= 9; i++) {
                int wordLength = descriptionArray[i].length();
                int paddingLeft = (wordLength - 1) / 2;
                int paddingRight = wordLength - paddingLeft - 1;
                System.out.print(" ".repeat(paddingLeft) + i + " ".repeat(paddingRight) + " ");
            }
            System.out.println("Enter which word you wish to edit:");
            int wordChoice = myObj.nextInt();
            myObj.nextLine();
            System.out.println("Enter how you wish to change it:");
            String newDescription = myObj.nextLine();
            descriptionArray[wordChoice] = newDescription;
            String newEntry = String.join(" ", descriptionArray);
            appointments.get(entryChoice).appointmentDescription = newEntry;
        }

        String appointmentToAdd = appointments.get(entryChoice).appointmentDate + ","
                + appointments.get(entryChoice).appointmentTime + "," +
                appointments.get(entryChoice).appointmentDescription;
        try {
            File f = new File(absolutePath + patient.id + "/afspraken.txt");
            List<String> lines = Files.readAllLines(f.toPath());
            lines.set(entryChoice, appointmentToAdd);
            System.out.println("LINES:");
            System.out.println(lines);

            FileWriter myWriter = new FileWriter(absolutePath + patient.id + "/afspraken.txt");
            myWriter.write(lines.get(0));
            for (int i = 1; i < lines.size(); i++) {
                myWriter.write("\n" + lines.get(i));
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sortAppointments(Patient patient) {
        Scanner myObj = new Scanner(System.in);
        System.out.println("1: Ascending");
        System.out.println("2: Descending");
        while (true) {
            int entryChoice = myObj.nextInt();
            myObj.nextLine();
            if (entryChoice == 1) {
                patient.appointmentList.sort(Comparator.comparing(Appointment::getAppointmentDate));
                break;
            } else if (entryChoice == 2) {
                patient.appointmentList.sort(Comparator.comparing(Appointment::getAppointmentDate).reversed());
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }

    public static void clearConsole() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}