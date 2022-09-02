package gr.aueb.dsapp.BackEnd;

import java.util.*;

import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class User implements Publisher, Consumer{
    public String channelName;
    public ArrayList<String> hashtagsPublished = new ArrayList<String>();
    public HashMap<String, Vector<Value>> userVideoFilesMap = new HashMap<String, Vector<Value>>();
    //public List<Broker> brokers;//
    public String ip_address;
    public int port;
    public ArrayList<BrokerInfo> broker_info;
    public ArrayList<VideoFile> videos_to_watch;
    public ServerSocket clientSocket;
    public Socket connectionSocket;
    //public String ans;

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String args[]){
        User user = new User();
        user.init(args[0], Integer.parseInt(args[1]));
        user.connect(args[2], Integer.parseInt(args[3]));
        Thread t = user.new Passive();
        t.start();
        boolean flag = true;
        while (flag){
            //System.out.println("1.Subscribe\n2.Play video\n3.Publish video\n4.Remove video\n5.Close");
            //String ans = keyboard.nextLine();
            if (ans.equals("1")){
                System.out.println("Give hashtag or channel name");
                ans = keyboard.nextLine();
                user.subscribe(ans);
            }else if(ans.equals("2")){

            }else if(ans.equals("3")){
                System.out.println("Give name");
                String name = keyboard.nextLine();
                System.out.println("Give path");
                String path = keyboard.nextLine();
                boolean flag2 = true;
                ArrayList<String> arr = new ArrayList<String>();
                while(flag2){
                    System.out.println("Give next hashtag or else write stop");
                    ans = keyboard.nextLine();
                    if (ans.equals("stop")){
                        break;
                    }
                    arr.add(ans);
                }
                user.publishVideo(path, name, arr);
            }else if(ans.equals("4")){
                System.out.println("Give name");
                ans = keyboard.nextLine();
                user.removeVideo(ans);
            }else if(ans.equals("5")){
                flag = false;
            }
        }
        keyboard.close();
        user.disconnect();
        System.exit(0);
    }*/

    public class Passive extends Thread{

        public void run(){
            try{
                while(true){
                    connectionSocket = clientSocket.accept();
                    Thread t = new ServerRequestHandler(connectionSocket);
                    t.start();
                }
            }catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    public class ServerRequestHandler extends Thread{
        ObjectInputStream in;
        ObjectOutputStream out;

        ServerRequestHandler(Socket serverSocket){
            try{
                this.in = new ObjectInputStream(serverSocket.getInputStream());
                this.out = new ObjectOutputStream(serverSocket.getOutputStream());
            } catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

        public void run(){
            try {
                String message = in.readUTF();
                ArrayList<String> message_tokens = new ArrayList<String>(Arrays.asList(message.split("delim")));
                if (message_tokens.get(2).equals("fetch videos for: ")){
                    push(out, message_tokens.get(4), message_tokens.get(3));
                }else if (message_tokens.get(2).equals("add") || message_tokens.get(2).equals("remove")){
                    updateBrokerInfo(message_tokens.get(0), Integer.parseInt(message_tokens.get(1)), message_tokens.get(2), message_tokens.get(3), message_tokens.get(4));
                }
            } catch(IOException ioException){
                ioException.printStackTrace();
            }finally{
                try{
                    in.close();
                    out.close();
                }catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
    
    public void init(String ip_address, int port){
        this.ip_address = ip_address;
        this.port = port;
        this.videos_to_watch = new ArrayList<VideoFile>();
    }

    public void connect(String ip_address, int port){
        Socket requestSocket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            requestSocket = new Socket(InetAddress.getByName(ip_address), port);
            objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(requestSocket.getInputStream());

            objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "connect" + "delim" + this.channelName);
            objectOutputStream.flush();

            this.broker_info = new ArrayList<BrokerInfo>((ArrayList<BrokerInfo>)objectInputStream.readObject());

            requestSocket.close();
            BrokerInfo b = hashTopic(this.channelName);
            requestSocket = new Socket(InetAddress.getByName("10.0.2.2"/*b.ip_address*/), b.port);
            objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());

            this.clientSocket = new ServerSocket(this.port);
            objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "my channel is: " + "delim" + this.channelName);
            objectOutputStream.flush();
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
    }

    public void updateBrokerInfo(String ip_address, int port, String request_type, String type, String topic){
        for (BrokerInfo binfo : this.broker_info){
            if (binfo.ip_address.equals(ip_address) & binfo.port == port){
                if (request_type.equals("add")){
                    if (type.equals("hashtag")){
                        if (!binfo.hashtags.contains(topic)){
                            binfo.hashtags.add(topic);
                        }
                    }else{
                        if (!binfo.channels.contains(topic)){
                            binfo.channels.add(topic);
                        }
                    }
                }else{
                    if (type.equals("hashtag")){
                        if (binfo.hashtags.contains(topic)){
                            binfo.hashtags.remove(topic);
                        }
                    }else{
                        if (binfo.channels.contains(topic)){
                            binfo.channels.remove(topic);
                        }
                    }
                }
            }
        }
    }

    public BrokerInfo hashTopic(String s){
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(s.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            int out = no.intValue();

            BrokerInfo b = new BrokerInfo();
            int min_output = Integer.MAX_VALUE;
            for (BrokerInfo binfo : this.broker_info){
                if (out < binfo.hash_output){
                    return binfo;
                }
                if (min_output > binfo.hash_output){
                    min_output = binfo.hash_output;
                    b = binfo;
                }
            }
            return b;
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void push(ObjectOutputStream objectOutputStream, String topic, String type){
        ArrayList<Value> vids;
        if (type.equals("hashtag")){
            vids = new ArrayList<Value>(this.userVideoFilesMap.get(topic));
        }else{
            vids = new ArrayList<Value>();
            for (String s : userVideoFilesMap.keySet()){
                vids.addAll(userVideoFilesMap.get(s));
            }
        }
        ArrayList<ArrayList<Value>> chunks = new ArrayList<ArrayList<Value>>();
        for (Value v : vids){
            chunks.add(this.generateChunks(v));
        }
        try{
            for (ArrayList<Value> arr : chunks){
                for (Value v : arr){
                    objectOutputStream.writeObject(v);
                    objectOutputStream.flush();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void publishVideo(String path, String videoname, ArrayList<String> associatedHashtags){
        Socket notifySocket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byte[] vid_in_bytes = Files.readAllBytes(Paths.get(path));
            Value vid = new Value(new VideoFile(videoname, this.channelName, null, null, null, null, null, associatedHashtags, vid_in_bytes));
            for (String h : associatedHashtags){
                boolean flag = false;
                if (!this.hashtagsPublished.contains(h)){
                    flag = true;
                    this.hashtagsPublished.add(h);
                }
                if (this.userVideoFilesMap.keySet().contains(h)){
                    this.userVideoFilesMap.get(h).add(vid);
                }else{
                    this.userVideoFilesMap.put(h, new Vector<Value>());
                    this.userVideoFilesMap.get(h).add(vid);
                }
                if (flag){
                    BrokerInfo binfo = this.hashTopic(h);
                    notifySocket = new Socket("10.0.2.2"/*binfo.ip_address*/, binfo.port);
                    objectOutputStream = new ObjectOutputStream(notifySocket.getOutputStream());

                    objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "new hashtag: " + "delim" + h + "delim" + this.channelName);
                    objectOutputStream.flush();
                }
            }
        } catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void removeVideo(String videoname){
        Socket notifySocket = null;
        ObjectOutputStream objectOutputStream = null;
        try{
            Iterator<String> iter1 = this.userVideoFilesMap.keySet().iterator();
            Iterator<Value> iter2;
            while(iter1.hasNext()){
                String h = iter1.next();
                iter2 = this.userVideoFilesMap.get(h).iterator();
                while(iter2.hasNext()){
                    Value v = iter2.next();
                    if (v.getVideoFile().getVideoName().equals(videoname)){
                        iter2.remove();
                        if (this.userVideoFilesMap.get(h).isEmpty()){
                            iter1.remove();
                            this.hashtagsPublished.remove(h);

                            BrokerInfo b = this.hashTopic(h);
                            notifySocket = new Socket(InetAddress.getByName("10.0.2.2"/*b.ip_address*/), b.port);
                            objectOutputStream = new ObjectOutputStream(notifySocket.getOutputStream());

                            objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "remove hashtag: " + "delim" + h);
                            objectOutputStream.flush();
                        }
                        break;
                    }
                }
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } /*finally {
            try {
                objectOutputStream.close();
                notifySocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }*/
    }

    public ArrayList<Value> generateChunks(Value v){
        ArrayList<Value> chunks = new ArrayList<Value>();
        byte[] arr = v.getVideoFile().getVideoFileChunk().clone();
        int default_chunk_size = 500000;
        int i = 0;
        while (i <= arr.length){
            byte[] arr2;
            if(i + default_chunk_size <= arr.length){
                arr2 = new byte[default_chunk_size];
            }else{
                arr2 = new byte[arr.length - i];
            }
            System.arraycopy(arr, i, arr2, 0, arr2.length);
            Value chunk = new Value(new VideoFile(v.getVideoFile()));
            chunk.getVideoFile().setVideoFileChunk(arr2);
            chunks.add(chunk);
            i += default_chunk_size;
        }
        return chunks;
    }

    public void subscribe(String s){
        this.videos_to_watch.clear();
        String ip_address = null;
        int port = 0;
        String type = null;
        if (s.charAt(0) == '#'){
            for (BrokerInfo binfo : this.broker_info){
                if (binfo.hashtags.contains(s)){
                    ip_address = binfo.ip_address;
                    port = binfo.port;
                    type = "hashtag";
                    break;
                }
            }
        }else{
            for (BrokerInfo binfo : this.broker_info){
                if (binfo.channels.contains(s)){
                    ip_address = binfo.ip_address;
                    port = binfo.port;
                    type = "channel";
                    break;
                }
            }
        }
        if(ip_address != null){
            Socket requestSocket = null;
		    ObjectOutputStream objectOutputStream = null;
		    ObjectInputStream objectInputStream = null;
            try{
                requestSocket = new Socket(InetAddress.getByName("10.0.2.2"), port);
                objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
			    objectInputStream = new ObjectInputStream(requestSocket.getInputStream());

			    objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "subscribe" + "delim" + type + "delim" + s);
                objectOutputStream.flush();

                ArrayList<Value> receiver = new ArrayList<Value>();
                try{
                    while(true){
                        receiver.add((Value) objectInputStream.readObject());
                    }
                }
                catch(EOFException eof){}
                
                HashMap<String, ArrayList<Value>> temp = new HashMap<String, ArrayList<Value>>();
                for (Value v : receiver){
                    String k = new String(v.getVideoFile().getChannelName() + v.getVideoFile().getVideoName());
                    if (temp.get(k) == null){
                        temp.put(k, new ArrayList<Value>());
                    }
                    temp.get(k).add(v);
                }

                for (String k : temp.keySet()){
                    int size = 0;
                    for (Value v : temp.get(k)){
                        size += v.getVideoFile().getVideoFileChunk().length;
                    }
                    VideoFile vid = new VideoFile(temp.get(k).get(0).getVideoFile());
                    vid.setVideoFileChunk(new byte[size]);
                    int i = 0;
                    for (Value v : temp.get(k)){
                        int j = 0;
                        while(j <= v.getVideoFile().getVideoFileChunk().length - 1){
                            vid.getVideoFileChunk()[i] = v.getVideoFile().getVideoFileChunk()[j];
                            j++;
                            i++;
                        }
                    }
                    this.videos_to_watch.add(vid);
                    /*FileOutputStream out = new FileOutputStream("video.mp4");
                    out.write(vid.getVideoFileChunk());
                    out.close();*/
                }
            } catch (UnknownHostException unknownHost) {
			    System.err.println("You are trying to connect to an unknown host!");
		    } catch (IOException ioException) {
			    ioException.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Hashtag or Channel not found");
        }
    }

    public void playData(String s, Value v){}

    public void disconnect(){
        Socket requestSocket = null;
        ObjectOutputStream objectOutputStream = null;
        //ObjectInputStream objectInputStream = null;
        try{
            BrokerInfo binfo = hashTopic(this.channelName);
            //for (BrokerInfo binfo : this.broker_info){
                requestSocket = new Socket(InetAddress.getByName("10.0.2.2"/*binfo.ip_address*/), binfo.port);
                objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
                objectOutputStream.writeUTF(this.ip_address + "delim" + this.port + "delim" + "disconnect");
                objectOutputStream.flush();
                //objectInputStream = new ObjectInputStream(requestSocket.getInputStream());
                //objectInputStream.readUTF();
            //}
            this.broker_info = null;
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } /*finally {
            try {
				objectOutputStream.close();
                requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
        }*/
    }
}