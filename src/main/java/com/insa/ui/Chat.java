package com.insa.ui;

import com.insa.message.MessageType;
import com.insa.model.Node;
import com.insa.model.Peer;
import com.insa.network.service.TCPMessageSenderService;
import com.insa.network.service.UDPMessageSenderService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Chat extends JFrame {

    private JTextArea chatWindow;
    private JTextField messageTextField;
    private JTextField notiTextField;

    private Homepage homepage;
    private Node node;
    private Peer cible;
    private File file;
    private boolean fileChosen = false;

    public Chat(Homepage homepage, Node node, Peer peer) throws IndexOutOfBoundsException {
        super();

        this.homepage = homepage;
        this.node = node;
        this.cible = peer;
        this.file = null;

        this.node.setChatWindowForPeer(peer,this);

        setTitle("you are talking to " + this.cible.getPseudonyme());

        System.out.println("you are talking to " + this.cible.getPseudonyme());

        messageTextField = new JTextField();

        messageTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke){
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    onSendButtonClicked(messageTextField.getText());
                }
            }
        });

        add(messageTextField, BorderLayout.SOUTH);

        chatWindow = new JTextArea(20,10);
        add(new JScrollPane(chatWindow));
        setSize(300,250);

        chatWindow.setEditable(false);

        this.notiTextField = new JTextField();
        notiTextField.setText("Chatting !!!");
        notiTextField.setEditable(false);

        JScrollPane jScrollPanel = new JScrollPane(chatWindow);

        JButton sendFileButton = new JButton("Choose File");
        sendFileButton.addActionListener(e -> onChooseFileButtonClicked());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> onSendButtonClicked(messageTextField.getText()));

        JButton closeDialogueButton = new JButton("Close Dialogue");
        closeDialogueButton.addActionListener(e -> onCloseDialogueButtonClicked());

        JPanel buttonPane = buildJPanelWith(sendFileButton, sendButton, closeDialogueButton);
        add(buttonPane, BorderLayout.PAGE_END);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPanel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                        .addComponent(notiTextField)
                                        .addComponent(messageTextField)
                                        .addComponent(buttonPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        )
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPanel, GroupLayout.Alignment.LEADING))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(notiTextField))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(messageTextField))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonPane))
                        )

        );

        messageTextField.requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static JPanel buildJPanelWith(JButton sendFileButton, JButton SendButton, JButton closeDialogueButton) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

        buttonPane.add(sendFileButton);
        buttonPane.add(SendButton);
        buttonPane.add(closeDialogueButton);

        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return buttonPane;
    }

    private void showLabel(String text) {
        // If text is empty return
        if(text.trim().isEmpty()) return;

        System.out.println("CALL IN Chat class : "+ text);

        // Otherwise, append text with a new line
        updateChatArea(text,true);

        // Set textfield and label text to empty string
        messageTextField.setText("");
    }

    public void updateChatArea(String text, boolean myself){
        if (myself) this.chatWindow.append(this.node.userName() + " >>> " + text +"\n");
        else this.chatWindow.append(this.cible.getPseudonyme() + ">>>" + text + "\n");
    }

    public void display() {
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void onCloseDialogueButtonClicked() {
        this.node.setChatWindowForPeer(cible,null);
        this.setVisible(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.dispose();
        if (homepage != null) {
            homepage.display();
        }
    }

    public void onSendButtonClicked(String text) {
        try {

            showLabel(text);

            System.out.println(cible.toString());

            new UDPMessageSenderService().sendMessageOn(cible, text.getBytes(), MessageType.MESS);

            if (this.fileChosen) {
                if (file != null) {
                    new UDPMessageSenderService().sendFileOn(cible, file);
                    this.fileChosen = false;
                    this.file = null;
                    this.notiTextField.setText("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onChooseFileButtonClicked() {

        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("Open this file: " +
                    chooser.getSelectedFile().getName());
        }

        this.file = chooser.getSelectedFile();
        this.notiTextField.setText(file.getName());

        this.fileChosen = true;
    }

    public String toString(){
        return "CALL IN toString of Chat class " + this.node.toString() +"are talking to " + this.cible.toString();
    }

}
