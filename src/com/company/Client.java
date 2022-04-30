package com.company;

import javafx.scene.control.PasswordField;
import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame{
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private JTextArea chatArea;
    private JTextField msgInputField;
    private boolean isAuth;

    private TextField loginField;
    private PasswordField passField;

    public Client() {
        openConnection();
        prepareGUI(); }

    private void prepareGUI() {
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(e -> sendMessage());
        msgInputField.addActionListener(e -> sendMessage());
        setVisible(true);
    }
    public void sendMessage() {
        if (!msgInputField.getText().trim().isEmpty()) {
            try{
                out.writeUTF(msgInputField.getText());
                msgInputField.setText("");
                msgInputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }

    public void openConnection() {
        try {
            Socket socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.startsWith("/authok")) {
                                setAuthorized(true);
                                break;
                            }
                            chatArea.append(strFromServer + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equalsIgnoreCase("/end")) {
                                break;
                            }
                            chatArea.append(strFromServer);
                            chatArea.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAuthorized(boolean b) {
        this.isAuth = isAuth;
    }

    public void onAuthClick() {
        try {
            out.writeUTF("/auth" + loginField.getText() + " " + passField.getText());
            passField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
        }
}

