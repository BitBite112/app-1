import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

class ZorgApp {
    public static void main(String[] args) {
        User user = new User(1, "Mart ElCamera", "huisarts");
        /* DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        String formatted = today.format(formatter);
        System.out.println("TODAY: " + formatted); */

        Administration administration = new Administration(user);
        administration.menu();

    }

}