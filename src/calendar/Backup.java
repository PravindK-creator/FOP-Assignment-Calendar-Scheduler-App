/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.io.*;
import java.util.Scanner;

/**
 * Feature: System Backup and Restore
 * Assigned to: Zhao
 */
public class Backup {
    private final String EVENT_FILE = "event.csv";
    private final String RECURRING_FILE = "recurring.csv";
    private final String ADDITIONAL_FILE="additional.csv";

    public void createBackup(String backupPath) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(backupPath))) {
            out.println("[EVENTS]"); 
            appendFileContent(EVENT_FILE, out);

            out.println("[RECURRING]");
            appendFileContent(RECURRING_FILE, out);
            
            out.println("[ADDITIONAL]");
            appendFileContent(ADDITIONAL_FILE, out);

            System.out.println("Success: Backup created at " + backupPath);
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    private void appendFileContent(String fileName, PrintWriter out) {
        File file = new File(fileName);
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(new FileInputStream(file))) {
            while (sc.hasNextLine()) {
                out.println(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Warning: " + fileName + " not found.");
        }
    }

    public void restoreBackup(String backupPath) {
        try (Scanner sc = new Scanner(new FileInputStream(backupPath));
             PrintWriter eventOut = new PrintWriter(new FileOutputStream(EVENT_FILE));
             PrintWriter additionalOut = new PrintWriter(new FileOutputStream(ADDITIONAL_FILE));   
             PrintWriter recurringOut = new PrintWriter(new FileOutputStream(RECURRING_FILE))) {
            
            String currentSection = "";
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.equals("[EVENTS]")) {
                    currentSection = "EVENT";
                    continue;
                } else if (line.equals("[RECURRING]")) {
                    currentSection = "RECURRING";
                    continue;
                }
                else if(line.equals("[ADDITIONAL]")){
                    currentSection = "ADDITIONAL";
                    continue;
                }

                if (currentSection.equals("EVENT")) {
                    eventOut.println(line);
                } else if (currentSection.equals("RECURRING")) {
                    recurringOut.println(line);
                }
                else if(currentSection.equals("ADDITIONAL")){
                    additionalOut.println(line);
                }
            }
            System.out.println("Success: System restored from backup.");
        } catch (IOException e) {
            System.err.println("Error during restoration: " + e.getMessage());
        }
    }
}
