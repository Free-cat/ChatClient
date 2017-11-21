import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatApp implements Runnable, ActionListener{
    private JFrame frame;
    private JLabel label1;
    private JTextField port;
    private JLabel label2;
    private JTextField ip;
    private JLabel label3;
    private JTextField name;
    private JButton startButton;
    private JButton finishButton;
    private JLabel label4;
    private JTextArea textArea;
    private JPanel panel;

    private InetAddress ipAddress;
    private Socket socket;
    private DataInputStream in;
    static ChatApp chatApp;
    String mes;
    Thread thread;

    public ChatApp(){
        //визуальный интерфейс
        frame=new JFrame("ChatClient");
        frame.setSize(200,500);


        panel = new JPanel();
        frame.add(panel);

        this.label1=new JLabel("Номер порта:");
        panel.add(label1);

        this.port=new JTextField("8901");
        panel.add(port);

        this.label2=new JLabel("Адрес сервера:");
        panel.add(label2);

        this.ip=new JTextField("127.0.0.1");
        panel.add(ip);

        this.label3=new JLabel("Имя пользователя:");
        panel.add(label3);

        this.name=new JTextField("No Name");
        panel.add(name);

        this.startButton=new JButton("Присоединиться");
        panel.add(startButton);

        this.finishButton=new JButton("Выйти");
        panel.add(finishButton);
        this.finishButton.setVisible(false);

        this.label4=new JLabel("Сообщения сервера");
        panel.add(label4);

        this.textArea =new JTextArea("............");
        panel.add(textArea);

        frame.setVisible(true);

        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e){
                        System.exit(0);
                    }
                }
        );

        startButton.addActionListener(this);
        finishButton.addActionListener(this);

        //запускаем поток
        thread=new Thread(this);
        thread.start();

    }

    private void start(){
        try {
            ipAddress = InetAddress.getByName(ip.getText()); // создаем объект который отображает вышеописанный IP-адрес.
            socket = new Socket(ipAddress, Integer.parseInt(port.getText())); // создаем сокет используя IP-адрес и порт сервера.
            //входной и выходной потоки данных
            OutputStream sout = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(sout);
            InputStream sin = socket.getInputStream();
            in = new DataInputStream(sin);

            //отпраляем имя серверу
            out.writeUTF(name.getText());

            this.finishButton.setVisible(true);
            this.startButton.setVisible(false);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finish(){
        try {
            this.finishButton.setVisible(false);
            this.startButton.setVisible(true);
            socket.close();//закрываем сокет
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String [] args){
                chatApp = new ChatApp();
    }

    @Override
    public void run() {
        while(true)
        {
            System.out.println("wait...");
            if(in!=null) { //если открытый входной поток данных
                System.out.println("read...");
                try {
                    mes = in.readUTF();//считываем сообщение
                    if (!mes.equals("")) {//если сообщение не пустое
                        System.out.println(mes);
                        textArea.append("\n"+mes);//выводим сообщение
                        if(!socket.isConnected()){//если сокет закрытый
                            break;//обрываем цикл
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(startButton)){
            start();
        }
        if(e.getSource().equals(finishButton)){
            finish();
        }
    }
}
