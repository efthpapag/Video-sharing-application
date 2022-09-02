package gr.aueb.dsapp.BackEnd;

public interface Node{
    
    public void init(String ip_address, int port);

    public void disconnect();
}