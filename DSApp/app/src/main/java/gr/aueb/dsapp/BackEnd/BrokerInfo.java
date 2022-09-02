package gr.aueb.dsapp.BackEnd;

import java.util.Vector;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BrokerInfo implements Serializable{
    public String ip_address;
    public int port;
    public int hash_output;
    public Vector<String> channels;
    public Vector<String> hashtags;

    BrokerInfo(){}
    
    BrokerInfo(String ip_address, int port, Vector<String> channels, Vector<String> hashtags){
        this.ip_address = ip_address;
        this.port = port;
        this.hash_output = hashBroker(new String(this.ip_address + this.port));
        this.channels = new Vector<String>(channels);
        this.hashtags = new Vector<String>(hashtags);
    }

    public int hashBroker(String s){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(s.getBytes());

            return new BigInteger(1, messageDigest).intValue();            
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}