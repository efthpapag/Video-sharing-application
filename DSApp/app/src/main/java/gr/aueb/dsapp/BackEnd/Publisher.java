package gr.aueb.dsapp.BackEnd;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public interface Publisher extends Node{

    public BrokerInfo hashTopic(String s);

    public void push(ObjectOutputStream stream, String s, String type);

    public ArrayList<Value> generateChunks(Value v);
}