package com.arkadiy.enter.smp3.services;

import android.os.AsyncTask;

import com.arkadiy.enter.smp3.activities.App;
import com.arkadiy.enter.smp3.activities.CommuicationActivity;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.dataObjects.Alert;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.dataObjects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by vadnu on 5/10/2019.
 */

public class SocketServices extends AsyncTask<String,Void,Void> {
    private Socket mSocket;
    private PrintWriter pw;
    private static final int NORMAL_CLOSURE_STAUS =1000 ;
    private OkHttpClient client;
    Request request;
    WebSocket ws;


    public SocketServices(){

        client = new OkHttpClient();
    }


    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            try {
                JSONObject subscribe =  new JSONObject().put("command","subscribe").put("user_id", User.getMyUserId()).put("user_name",User.getMyUserName());
                webSocket.send(subscribe.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output(bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STAUS,null);
            //output("Closing :"+code +" / "+reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            //output("Error: "+t.getMessage());
        }


    }

    private void start() {
        if(ws==null){
            request = new Request.Builder().url(AppConfig.SOCKET_SERVER_URL).build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            ws = client.newWebSocket(request, listener);
        }

    }

    @Override
    protected Void doInBackground(String... strings) {

        try {
            start();



        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String message){
        try {
            Thread thread=new Thread();
            thread.run();
            pw=new PrintWriter(mSocket.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void output(final String txt) {
        try {

            JSONObject json=new JSONObject(txt.toString());
            String command=json.getString("command");
            switch (command){
                case "subscribe":
                    if (App.getContext().getClass() == CommuicationActivity.class){

                        CommuicationActivity.addOnlineUser(json);
                    }
                    else {
                        Store.doSubscribe(json);
                    }
                    break;

                case "task":
                    User.addNewTask(json);


                    break;

                    case "alert":

                        Alert alert = new Alert(json);
                        User.getAlertFromSocket(alert);
                        break;

                    //{"command":"unsubscribe","user_name":"arkadi","user_id":2}
                   case "unsubscribe":
                        if (App.getContext().getClass() == CommuicationActivity.class){

                            CommuicationActivity.removeUser(json.getString("user_name"));

                        }
                       break;
                case "new_alert":

                    if (App.getContext().getClass() == CommuicationActivity.class){

                        Store.setNewAlert(json);
                    }

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }


    }



}
