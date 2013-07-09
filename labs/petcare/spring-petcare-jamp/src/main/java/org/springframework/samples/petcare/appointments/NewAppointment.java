package org.springframework.samples.petcare.appointments;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class NewAppointment {

	@NotNull
	private Long dateTime;
	
	@NotNull
	private Long patientId;

	@NotNull
	private Long doctorId;
	
	@Max(value=255)
	private String reason;

    public Long getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DateTime: ").append(getDateTime()).append(", ");
        sb.append("PatientId: ").append(getPatientId()).append(", ");
        sb.append("DoctorId: ").append(getDoctorId()).append(", ");
        sb.append("Reason: ").append(getReason());
        return sb.toString();
    }
}
