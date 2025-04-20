/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.model;

/**
 *
 * @author ASUS
 */
public class SensorData {
    private int boatId;
    private String sensorType;
    private String value;

    public SensorData(int boatId, String sensorType, String value) {
        this.boatId = boatId;
        this.sensorType = sensorType;
        this.value = value;
    }

    public int getBoatId() { return boatId; }
    public String getSensorType() { return sensorType; }
    public String getValue() { return value; }
}
