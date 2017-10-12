package Servers;

import BankOps.NBOpsImplementation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NBServer   {

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



        NBOpsImplementation nbOpsImplementation = new NBOpsImplementation();
        Registry registry = LocateRegistry.createRegistry(8876);
        registry.bind("NB" , nbOpsImplementation);
        System.out.println("NB Server ready!");

        System.out.println("NB implementaion is ready!");
        DatagramSocket serverSocket = new DatagramSocket(8876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());

            System.out.println("RECEIVED: " + sentence.trim());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //send the count
            int numOfClient= nbOpsImplementation.hashArraylistSize();

            String accountCount = String.valueOf(numOfClient);
            //convert it to Byte
            sendData = accountCount.getBytes();

            //Send the connection
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            //send the data...
            serverSocket.send(sendPacket);

        }

    }


}


