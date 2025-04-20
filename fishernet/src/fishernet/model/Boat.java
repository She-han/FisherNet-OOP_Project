/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.model;

public class Boat {

    private int id;
    private String name;
    private String registrationNo;
    private String qrCodePath;
    private String sensors;

    public Boat(String name, String regNo, String qrPath, String sensorsJson) {
        this.name = name;
        this.registrationNo = regNo;
        this.qrCodePath = qrPath;
        this.sensors = sensorsJson;
    }
    // second constructor for fishStock form
    public Boat(String name, String registrationNo, String qrCodePath) {
        this.name = name;
        this.registrationNo = registrationNo;
        this.qrCodePath = qrCodePath;
        this.sensors = "{}"; // Default empty JSON string
    }

    public String getSensors() {
        return sensors;
    }

    public void setSensors(String sensors) {
        this.sensors = sensors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    @Override
    public String toString() {
        return registrationNo;
    }
}
