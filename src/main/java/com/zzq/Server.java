package com.zzq;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args){
        //startServer=new startServer();
        new startServer().start();//new 一个线程对象开始启动（由于startServer类继承了Thread）
    }
    public static ArrayList<UserThread> socketList=new ArrayList<UserThread>();//创建一个泛型是UserThread（UserThread是下面的一个类）的动态数组
    public static startServer startServer;
    static class startServer extends Thread{
        public void run(){
            try{
                ServerSocket serverSocket = new ServerSocket(6666);
                //创建端口值为：6666的ServerSocket对象
                while(true){//死循环
                    Socket socket = serverSocket.accept();//创建socket对象，用于接受客户端的请求
                    System.out.println(""+socket);//用于显示客户端的IP地址，客户端的端口号，以及电脑的端口号
                    UserThread userThread = new UserThread(socket);//通过下面定义的UserTread的有参构造，创建userThread对象
                    socketList.add(userThread);
                    new Thread(userThread).start();//开启输入输出流线程
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    static class UserThread implements Runnable{
        private Socket skt;
        private DataOutputStream dos;
        private DataInputStream dis;
        public DataOutputStream getDos(){//返回输出流
            return dos;
        }
        public void setDos(DataOutputStream dos){//给输出流传递参数
            this.dos=dos;
        }
        public DataInputStream getDis(){//返回输入流
            return dis;
        }
        public void setDis(DataInputStream dis){//给输入流传递参数
            this.dis=dis;
        }
        public UserThread(Socket socket){//构造有参构造
            skt=socket;
        }
        @Override
        public void run(){
            try{
                dos= new DataOutputStream(skt.getOutputStream());//获取输出流（准备从服务器给其他的客户端发消息）
                dis= new DataInputStream(skt.getInputStream());//接收客户端发过来的消息（输入流）
                String recMsg ="";
                while(true){//使服务器无限循环
                    if(!"".equals(recMsg=dis.readUTF())){//读取输入流的消息，并把消息传到recMsg中
                        System.out.println("收到一条消息: "+ recMsg);//显示：收到一条消息+“传入的消息”
                        for(UserThread s:socketList){//增强for循环
                            if(s.equals(this)){
                                continue;
                            }
                            try{
                                s.getDos().writeUTF("服务器收到了你的消息：" + recMsg);//将UTF-8的字符串写入字节流

                            }catch(IOException e){
                                socketList.remove(s);//将s从动态数组socketList中删除
                                e.printStackTrace();
                            }
                        }
                        recMsg="";//recMsg内容重新刷新
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
