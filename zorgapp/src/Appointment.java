import java.time.LocalDate;

public class Appointment {
    LocalDate appointmentDate;
    String appointmentTime;
    String appointmentDescription;

    Appointment(LocalDate appointmentDate, String appointmentTime, String appointmentDescription) {
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentDescription = appointmentDescription;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }
}
