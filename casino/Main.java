/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package casino;

import casino.ui.CasinoWindow;

import javax.swing.*;

/**
 * @author Chase Bradley, Joseph Dial, Kaleb Hankerson
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CasinoWindow::new);
    }
}