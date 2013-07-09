package org.springframework.samples.petcare.appointments;

import org.joda.time.DateTime;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class Appointment {

	private Long id;

	private DateTime dateTime;

	private Long doctorId;

	private String patient;

	private String client;

	private String clientPhone;

	private String reason;

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public DateTime getDateTime() {
        return this.dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatient() {
        return this.patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientPhone() {
        return this.clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("DateTime: ").append(getDateTime()).append(", ");
        sb.append("DoctorId: ").append(getDoctorId()).append(", ");
        sb.append("Patient: ").append(getPatient()).append(", ");
        sb.append("Client: ").append(getClient()).append(", ");
        sb.append("ClientPhone: ").append(getClientPhone()).append(", ");
        sb.append("Reason: ").append(getReason());
        return sb.toString();
    }
}