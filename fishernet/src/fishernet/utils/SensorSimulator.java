/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.utils;
import fishernet.dao.SensorDAO;
import fishernet.model.SensorData;

import java.util.Random;
/**
 *
 * @author ASUS
 */
public class SensorSimulator {
    public static void sendMockData(int boatId) {
        String[] sensors = {"temperature", "humidity", "water_level", "vibration", "load_weight"};
        Random rand = new Random();

        for (String sensor : sensors) {
            String value = switch (sensor) {
                case "temperature" -> String.valueOf(20 + rand.nextInt(10));
                case "humidity" -> String.valueOf(60 + rand.nextInt(20));
                case "water_level" -> String.valueOf(rand.nextInt(5));
                case "vibration" -> rand.nextBoolean() ? "normal" : "irregular";
                case "load_weight" -> String.valueOf(100 + rand.nextInt(200));
                default -> "N/A";
            };

            SensorDAO.saveSensorData(new SensorData(boatId, sensor, value));
        }

        System.out.println("✅ Simulated sensor data saved for boat ID: " + boatId);
    }

    public static void main(String[] args) {
        sendMockData(1); // Use actual boat ID
    }
}
