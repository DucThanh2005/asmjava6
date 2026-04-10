package com.example.carstore.mode;

public class SupportRequest {
    private String name;
    private String phone;
    private String type;
    private String content;
    private String status;

    public SupportRequest(String name, String phone, String type, String content) {
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.content = content;
        this.status = "Đang xử lý";
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}