package Servers;

import BankOps.BCOpsImplementation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BCServer   {

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




        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        Registry registry = LocateRegistry.createRegistry(6876);
        registry.bind("BC" , bcOpsImplementation);
        System.out.println("BC Server ready!");

        System.out.println("BC implementaion is ready!");
        DatagramSocket serverSocket = new DatagramSocket(6876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true)
        {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());

            System.out.println("RECEIVED: " + sentence.trim());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //send the count
            int numOfClient= bcOpsImplementation.hashArraylistSize();

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


