package com.example.carstore.entity;

import javax.persistence.*;

@Entity
@Table(name = "support_request")
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String phone;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String username;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String type;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String content;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String status;

    @Column(name = "car_info", columnDefinition = "NVARCHAR(255)")
    private String carInfo;

    @Column(name = "service_type", columnDefinition = "NVARCHAR(255)")
    private String serviceType;

    @Column(name = "appointment_date", columnDefinition = "NVARCHAR(50)")
    private String appointmentDate;

    @Column(name = "appointment_time", columnDefinition = "NVARCHAR(50)")
    private String appointmentTime;

    public SupportRequest() {
        this.status = "Chờ xử lý";
    }

    public SupportRequest(String name, String phone, String type, String content) {
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.content = content;
        this.status = "Chờ xử lý";
    }

    public SupportRequest(String name, String phone, String type, String content,
                          String carInfo, String serviceType,
                          String appointmentDate, String appointmentTime) {
        this(name, phone, type, content);
        this.carInfo = carInfo;
        this.serviceType = serviceType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}