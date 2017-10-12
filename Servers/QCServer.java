package Servers;

import BankOps.QCOpsImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class QCServer {


    /*
        qc->5876
        BC->6876
        MB->7876
        NB->8876

       */

    public static void main(String[] args) throws Exception {

        serverRun();
    }


    private static void serverRun() throws Exception {

        QCOpsImplementation qcOpsImplementation = new QCOpsImplementation();
        Registry registry = LocateRegistry.createRegistry(5876);
        registry.bind("QC", qcOpsImplementation);

        System.out.println("QC Server ready for RMI!");

        //start the server to recive calls from other server


        System.out.println("QC server is ready for client!");
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(5876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sentence = new String(receivePacket.getData());

            System.out.println("RECEIVED: " + sentence.trim());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //send the coount
            //send the coount
            int numOfClient = qcOpsImplementation.hashArraylistSize();
            String accountCount = String.valueOf(numOfClient);
            //convert it to Byte
            sendData = accountCount.getBytes();

            //Send the connection
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            //send the data...
            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}


