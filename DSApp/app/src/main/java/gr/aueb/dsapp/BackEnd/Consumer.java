package gr.aueb.dsapp.BackEnd;

public interface Consumer extends Node{
    public void subscribe(String s);

    public void playData(String s, Value v);
}