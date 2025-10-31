import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

class ZorgApp {
    public static void main(String[] args) {
        User user = null;
        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your userID: ");
            String givenId = sc.nextLine();
            System.out.println("Enter your password: ");
            String givenPassword = sc.nextLine();
            if (User.checkPassword(givenId, givenPassword)) {
                user = makeUser(givenId);
                break;
            } else {
                System.out.println("Invalid username or password");
            }
        }
        /* DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        String formatted = today.format(formatter);
        System.out.println("TODAY: " + formatted); */

        Administration administration = new Administration(user);
        administration.menu();
    }

    static User makeUser(String userID) {
        String absolutePath = "C:/zorgAppData/Users/" + userID + "/";
        int userInt = 0;
        String userName = null;
        String userProfession = null;
        try {
            File f = new File(absolutePath + "Info.txt");
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                if (data[0].equals(userID)) {
                    userInt = Integer.parseInt(data[0]);
                    userName = data[1];
                    userProfession = data[2];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(userInt, userName, userProfession);
    }
}