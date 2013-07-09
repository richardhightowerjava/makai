package org.springframework.samples.petcare.appointments;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean(settersByDefault=false)
@RooToString
public final class AppointmentMessage {

	private MessageType type;
	
	private Appointment appointment;
	
	public AppointmentMessage(MessageType type, Appointment appointment) {
		this.type = type;
		this.appointment = appointment;
	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(getType()).append(", ");
        sb.append("Appointment: ").append(getAppointment());
        return sb.toString();
    }

    public MessageType getType() {
        return this.type;
    }

    public Appointment getAppointment() {
        return this.appointment;
    }

    public enum MessageType {
		APPOINTMENT_ADDED, APPOINTMENT_DELETED
	}
}
