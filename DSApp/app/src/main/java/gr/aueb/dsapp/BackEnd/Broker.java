package gr.aueb.dsapp.BackEnd;

import java.util.*;
import java.io.*;
import java.net.*;


public class Broker implements Node{
    public Vector<Consumer> registeredConsumers = new Vector<Consumer>();
    public Vector<Publisher> registeredPublishers = new Vector<Publisher>();
    public Vector<Broker> brokers = new Vector<Broker>();
    public Vector<String> channelsManaged = new Vector<String>();
    public Vector<String> hashtagsManaged = new Vector<String>();
    public String ip_address;
    public int port;
    ServerSocket serverSocket;
    Socket connectionSocket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.init(args[0], Integer.parseInt(args[1]));
        broker.connect();
        try{
            while(true){
                broker.connectionSocket = broker.serverSocket.accept();
                broker.objectOutputStream = new ObjectOutputStream(broker.connectionSocket.getOutputStream());
                broker.objectInputStream = new ObjectInputStream(broker.connectionSocket.getInputStream());
                Thread t = broker.new RequestHandler(broker.connectionSocket);
                t.start();
            }
        } catch(IOException ioException){
        }
    }

    class RequestHandler extends Thread{
        ObjectInputStream in;
	    ObjectOutputStream out;

        RequestHandler(Socket clientSocket){
            this.in = objectInputStream;
            this.out = objectOutputStream;
        }

        public void run(){
            try {
                String message = in.readUTF();
                ArrayList<String> message_tokens = new ArrayList<String>(Arrays.asList(message.split("delim")));
                if (message_tokens.get(2).equals("add") || message_tokens.get(2).equals("remove")){
                    updateBrokerInfo(message_tokens.get(0), Integer.parseInt(message_tokens.get(1)), message_tokens.get(2), message_tokens.get(3), message_tokens.get(4));
                    Socket updateSocket = null;
                    ObjectOutputStream objectOutputStream = null;
                    try{
                        for (Consumer c : registeredConsumers){
                            updateSocket = new Socket("localhost",((User)c).port);
                            objectOutputStream = new ObjectOutputStream(updateSocket.getOutputStream());
                            objectOutputStream.writeUTF(message_tokens.get(0) + "delim" + message_tokens.get(1) + "delim" + message_tokens.get(2) + "delim" + message_tokens.get(3) + "delim" + message_tokens.get(4));
                            objectOutputStream.flush();
                        }   
                    } catch(IOException ioException){
                    } finally {
                        try {
                            in.close();
                            out.close();
                        } catch (IOException ioException) {
                        }
                    }            
                }else{
                    User u = new User();
                    u.init(message_tokens.get(0), Integer.parseInt(message_tokens.get(1)));
                    if (message_tokens.get(2).equals("connect")){
                        u.channelName=message_tokens.get(3);
                        accept_connection_with_consumer(u);
                    }else if(message_tokens.get(2).equals("my channel is: ")){
                        u.channelName=message_tokens.get(3);
                        registerChannel(u);
                    }else if(message_tokens.get(2).equals("disconnect")){
                        disconnection_with_user(u);
                    }else if(message_tokens.get(2).equals("new hashtag: ")){
                        u.channelName = message_tokens.get(4);
                        registerVideo(u, message_tokens.get(3));
                    }else if(message_tokens.get(2).equals("remove hashtag: ")){
                        removeVideo(u, message_tokens.get(3));
                    }else if(message_tokens.get(2).equals("subscribe")){
                        pull(u, out, message_tokens.get(3), message_tokens.get(4));
                    }
                }
            } catch (IOException e) {
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                }
            }
        }
    }

    public void init(String ip_address, int port){
        this.ip_address = ip_address;
        this.port = port;
    }
    

    public void connect(){
        try{
            URL url = getClass().getResource("Brokers.txt");
            File f = new File(url.getPath());
            Scanner reader = new Scanner(f);
            while(reader.hasNextLine()){
                String str1 = reader.nextLine();
                int p = Integer.parseInt(reader.nextLine());
                if (!(str1.equals(this.ip_address) & p == this.port)){
                    Broker temp = new Broker();
                    temp.init(str1, p);
                    this.brokers.add(temp);
                }
            }
            reader.close();

            serverSocket = new ServerSocket(this.port);
        }catch (FileNotFoundException e){
        }catch (IOException ioException){
        }
    }

    public void updateNodes(String request_type, String type, String topic){
        ObjectOutputStream objectOutputStream = null;
        Socket updateSocket = null;

        try{
            for (Broker b : this.brokers){
                updateSocket = new Socket(InetAddress.getByName(b.ip_address), b.port);
                objectOutputStream = new ObjectOutputStream((updateSocket.getOutputStream()));
                objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + request_type + "delim" + type + "delim" + topic);
                objectOutputStream.flush();
            }
            for (Consumer c : this.registeredConsumers){
                updateSocket = new Socket("localhost",((User)c).port);
                objectOutputStream = new ObjectOutputStream((updateSocket.getOutputStream()));
                objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + request_type + "delim" + type + "delim" + topic);
                objectOutputStream.flush();

            }
        }catch(IOException ioException){
        }
    }

    public void accept_connection_with_consumer(Consumer c){
        this.registeredConsumers.add(c);
        ArrayList<BrokerInfo> binfo = new ArrayList<BrokerInfo>();
        binfo.add(new BrokerInfo(this.ip_address, this.port, this.channelsManaged, this.hashtagsManaged));
        for (Broker b : this.brokers){
            binfo.add(new BrokerInfo(b.ip_address, b.port, b.channelsManaged, b.hashtagsManaged));
        }
		
        try{
            this.objectOutputStream.writeObject(binfo);
			this.objectOutputStream.flush();

        } catch (UnknownHostException unknownHost) {
		} catch (IOException ioException) {
        }
    }

    public void disconnection_with_user(User user){
        String channelName = null;
        Iterator<Consumer> iter1 = this.registeredConsumers.iterator();
        while(iter1.hasNext()){
            User c = (User) iter1.next();
            if ((c).ip_address.equals(user.ip_address) & (c).port == user.port){
                channelName=c.channelName;
                iter1.remove();
            }
        }
        Iterator<Publisher> iter2 = this.registeredPublishers.iterator();
        while(iter2.hasNext()){
            User p = (User) iter2.next();
            if (p.ip_address.equals(user.ip_address) & p.port == user.port){
                channelName=p.channelName;
                iter2.remove();
            }
        }
        this.updateNodes("remove", "channel name", channelName);
    }

    public void registerChannel(User publisher){
        this.registeredPublishers.add(publisher);
        this.channelsManaged.add(publisher.channelName);
        this.updateNodes("add", "channel name", publisher.channelName);
    }

    public void pull(Consumer c, ObjectOutputStream objectOutputStream, String type, String s){
        boolean flag = false;
        for (Consumer cns : this.registeredConsumers){
            if (/*((User)cns).ip_address.equals(((User)c).ip_address) & */((User)cns).port == ((User)c).port){
                flag = true;
                break;
            }
        }
        if (!flag){
            this.registeredConsumers.add(c);
        }
        ArrayList<Publisher> publishers_to_ask = new ArrayList<Publisher>();
        if (type.equals("hashtag")){
            for (Publisher p : this.registeredPublishers){
                if (((User)p).hashtagsPublished.contains(s)){
                    publishers_to_ask.add(p);
                }
            }
        }else{
            for (Publisher p : this.registeredPublishers){
                if (((User)p).channelName.equals(s)){
                    publishers_to_ask.add(p);
                }
            }
        }
        Vector<Value> v = new Vector<Value>();
        Vector<Thread> t_holder = new Vector<Thread>();
        for (Publisher p : publishers_to_ask){
            if (filterConsumers(c, p)){
                try{
                    class Request extends Thread{
                        Socket requestSocket;
                        ObjectOutputStream objectOutputStream;
                        ObjectInputStream objectInputStream;

                        Request(Socket requestSocket){
                            this.requestSocket = requestSocket;
                        }

                        public void run(){
                            try{
                                objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
                                objectInputStream = new ObjectInputStream(requestSocket.getInputStream());

                                objectOutputStream.writeUTF(ip_address + "delim" + port + "delim" + "fetch videos for: " + "delim" + type + "delim" + s);
                                objectOutputStream.flush();
                                try{
                                    while(true){
                                        v.add((Value) objectInputStream.readObject());
                                    }
                                } catch(EOFException eof){
                                    if (v.isEmpty()){
                                    }
                                }
                            }catch (UnknownHostException unknownHost) {
                            }catch (IOException ioException) {
                            }catch (ClassNotFoundException e) {
                            }
                        }
                    }
                    Socket requestSocket = new Socket("localhost", ((User)p).port);
                    Thread t = new Thread(new Request(requestSocket));
                    t_holder.add(t);
                    t.start();
                }catch (UnknownHostException unknownHost) {
                }catch (IOException ioException) {
                }
            }
        }
        try{
            for (Thread t: t_holder){
                t.join();
            }
            for (Value chunk : v){
                objectOutputStream.writeObject(chunk);
            }
        }catch(InterruptedException e){
        }catch(IOException ioException){
        }

    }

    public boolean filterConsumers(Consumer cns, Publisher pbl){
        User c = (User) cns;
        User p = (User) pbl;
        return !((c.ip_address.equals(p.ip_address)) & (c.port == p.port));
    }

    public void registerVideo(User user, String s){
        boolean flag = false;
        for (Publisher p : this.registeredPublishers){
            if (((User)p).ip_address.equals(user.ip_address) & ((User)p).port == user.port){
                flag = true;
                ((User)p).hashtagsPublished.add(s);
                break;
            }
        }
        if (!flag){
            user.hashtagsPublished.add(s);
            this.registeredPublishers.add(user);
        }
        for (String h:this.hashtagsManaged){
            if (h.equals(s)){
                return ;
            }
        }
        this.hashtagsManaged.add(s);
        this.updateNodes("add", "hashtag", s);
    }

    public void removeVideo(User user, String s){
        for (Publisher p : this.registeredPublishers){
            if (((User)p).ip_address.equals(user.ip_address) & ((User)p).port == user.port){
                ((User)p).hashtagsPublished.remove(s);
                if (((User)p).hashtagsPublished.isEmpty()){
                    this.registeredPublishers.remove(p);
                }
                break;
            }
        }
        for (Publisher p : this.registeredPublishers){
            for(String hashtag:((User)p).hashtagsPublished){
                if (hashtag.equals(s)){
                    return ;
                }
            }
        }
        this.updateNodes("remove", "hashtag", s);
    }

    public void disconnect(){
        try{
            serverSocket.close();
        }catch (IOException ioException){
        }
    }

    public void updateBrokerInfo(String ip_address, int port, String request_type, String type, String topic){
        for (Broker b : this.brokers){
            if (b.ip_address.equals(ip_address) & b.port == port){
                if (request_type.equals("add")){
                    if (type.equals("hashtag")){
                        if (!b.hashtagsManaged.contains(topic)){
                            b.hashtagsManaged.add(topic);
                        }
                    }else{
                        if (!b.channelsManaged.contains(topic)){
                            b.channelsManaged.add(topic);
                        }
                    }
                }else{
                    if (type.equals("hashtag")){
                        if (b.hashtagsManaged.contains(topic)){
                            b.hashtagsManaged.remove(topic);
                        }
                    }else{
                        if (b.channelsManaged.contains(topic)){
                            b.channelsManaged.remove(topic);
                            if(this.registeredConsumers.contains(topic)){
                                this.registeredConsumers.remove(topic);
                            }
                        }
                    }
                }
            }
        }
    }
}