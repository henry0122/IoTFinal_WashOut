package com.example.azure.washout;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ShowFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String firstData = ""; // tmp for the data that just get from server (eg. Temp_Display,1496721232964,26.1)
    private String firstData2 = "";

    private HashMap<String,ArrayList<String>> allData = new HashMap<String, ArrayList<String>>();


    public static final int MEG_FREE = 6666;
    public static final int MEG_BUSY = 9527;
    private View mContentView = null;
    private ImageView freeMachine;
    private ImageView busyMachine ;
    private ImageView loading ;
    private TextView freeText ;
    private TextView busyText ;

    private boolean isWashing = false;
    private boolean isDrying = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.fragment_show, null);
        freeMachine = (ImageView) mContentView.findViewById(R.id.freemachine);
        busyMachine = (ImageView) mContentView.findViewById(R.id.busymachine);
        loading = (ImageView) mContentView.findViewById(R.id.wait);
        freeText = (TextView) mContentView.findViewById(R.id.free);
        busyText = (TextView) mContentView.findViewById(R.id.busy);

        Thread t = (new Thread(new Show(freeMachine, busyMachine, loading, freeText, busyText)));
        t.start();

        return mContentView;
    }

    Handler handler = new Handler()
    {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 當收到的Message的代號為我們剛剛訂的代號就做下面的動作。
                case MEG_BUSY:
                    // 重繪UI
                    freeMachine.setVisibility(View.GONE);
                    busyMachine.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    freeText.setVisibility(View.GONE);
                    busyText.setVisibility(View.VISIBLE);

                    break;

                case MEG_FREE:
                    freeMachine.setVisibility(View.VISIBLE);
                    busyMachine.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    freeText.setVisibility(View.VISIBLE);
                    busyText.setVisibility(View.GONE);
                    break;

            }
            super.handleMessage(msg);
        }

    };


    // HTTP GET request
    private void sendGet() throws Exception {

        String url = "http://api.mediatek.com/mcs/v2/devices/DAhnGAC7/datachannels/Acc/datapoints.csv";
        String url2 = "http://api.mediatek.com/mcs/v2/devices/DAhnGAC7/datachannels/Light/datapoints.csv";

        URL obj = new URL(url);
        URL obj2 = new URL(url2);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con2.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("deviceKey", "GAnr08h5z0m079qm");
        con.setRequestProperty("Content-Type","text/csv");
        con.setRequestProperty("Connection", "close");
        con2.setRequestProperty("deviceKey", "GAnr08h5z0m079qm");
        con2.setRequestProperty("Content-Type","text/csv");
        con2.setRequestProperty("Connection", "close");


        try {

            InputStream in = con.getInputStream();
            InputStream in2 = con2.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);
            InputStreamReader isw2 = new InputStreamReader(in2);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                String tmp = String.valueOf(current);
                firstData += tmp;
                data = isw.read();
            }
            int data2 = isw2.read();
            while (data2 != -1) {
                char current2 = (char) data2;
                String tmp2 = String.valueOf(current2);
                firstData2 += tmp2;
                data2 = isw2.read();
            }
            parseData(firstData);
            firstData = "";
            parseData(firstData2);
            firstData2 = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }

    } // end of sendGet()

    // parse the csv and save into HashMap
    public void parseData(String in)
    {
        String name;
        String recordAt;
        String value;
        String[] fuck ;
        ArrayList<String> tmp = new ArrayList<>(50);
        ArrayList<String> temp;

        // build on the condition of only three arguments (eg. test_channel(arg1),1432538734355(arg2),100(arg3))
        fuck = in.split(",");
        name = fuck[0];
        //recordAt = fuck[1];
        value = fuck[2];

        if(this.allData.get(name) == null) // no key
        {
            tmp.add(value);
            this.allData.put(name,tmp);
        }
        else
        {
            temp = this.allData.get(name);
            if(temp.size() == 50)
            {
                temp.clear();
            }
            temp.add(value);
            this.allData.put(name,temp);
        }


        for (HashMap.Entry<String, ArrayList<String>> entry : allData.entrySet())
        {
            for(String it : entry.getValue())
            {
                System.out.println("Key = " + entry.getKey() + ", Value = " + it);
            }
        }

    }

    public void changeView(ImageView i, ImageView b, ImageView l, TextView f, TextView tv)
    {
        // use the channelId directly
        if (allData.get("Light").isEmpty() || allData.get("Acc").isEmpty()) {}
        else // can compare the data
        {
            if(!isWashing)
            {
                int tmp = allData.get("Light").size();
                String s = allData.get("Light").get(tmp-1); // the latest data
                int result = Integer.parseInt(s);

                if(result > 40) // start washing
                {
                    isWashing = true;
                    System.out.println("fuck washing~~~");

                    Message m = new Message();
                    // 定義 Message的代號，handler才知道這個號碼是不是自己該處理的。
                    m.what = MEG_BUSY;
                    handler.sendMessage(m);
                }
                else
                {
                    Message m = new Message();
                    // 定義 Message的代號，handler才知道這個號碼是不是自己該處理的。
                    m.what = MEG_FREE;
                    handler.sendMessage(m);
                }
            }
            else
            {
                if(!isDrying)
                {

                    int tmp = allData.get("Acc").size();
                    String s = allData.get("Acc").get(tmp - 1); // the latest data
                    int result = Integer.parseInt(s);

                    if (result > 40 || result < -40) // drying clothes
                    {
                        System.out.println("fuck drying~~~");
                        isDrying = true;
                    }

                    // stay until start drying
                    while(result < 40 && result > -40) {
                        System.out.println("fuck loop~~~");
                        try {
                            sendGet();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tmp = allData.get("Acc").size();
                        s = allData.get("Acc").get(tmp - 1); // the latest data
                        result = Integer.parseInt(s);

                        if (result > 40 || result < -40) // drying clothes
                        {
                            System.out.println("fuck drying~~~");
                            isDrying = true;
                            break;
                        }
                        int iy = 0 ;
                        while(iy < 10000000)
                        {
                            iy++;
                        }
                    }
                }
                else
                {
                    int tmp = allData.get("Acc").size();
                    String s = allData.get("Acc").get(tmp - 1); // the latest data
                    int result = Integer.parseInt(s);

                    if(-10 < result && result < 10)
                    {
                        isDrying = false;
                        isWashing = false;

                        Message m = new Message();
                        // 定義 Message的代號，handler才知道這個號碼是不是自己該處理的。
                        m.what = MEG_FREE;
                        handler.sendMessage(m);
                        // stay until the light turn off
                        while (true)
                        {
                            try {
                                sendGet();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int temp = allData.get("Light").size();
                            String ss = allData.get("Light").get(temp-1); // the latest data
                            int res = Integer.parseInt(ss);
                            if(res < 40)
                            {
                                break;
                            }
                        }
                    }
                }

            }

        }
    }

    private class Show implements Runnable {


        // WHAt the fuck is this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // fucking shit nullable
        // fucking get the imageview to change in handler like this shit way~~~~~
        // bullshit all these bullshit
        private ImageView freeMach;
        private ImageView busyMach ;
        private ImageView load ;
        private TextView freeTex ;
        private TextView busyTex ;

        Show(ImageView i, ImageView b, ImageView l, TextView f, TextView tv)
        {
            freeMach = i;
            busyMach = b;
            load = l;
            freeTex = f;
            busyTex = tv;
        }

        @Override
        public void run() {
            try {
                while(true)
                {
                    // wish to get data once every 3 seconds
                    sendGet();
                    changeView(freeMach, busyMach, load, freeTex, busyTex); // detect whether isWashing
                    if(isWashing && !isDrying)
                    {
                        System.out.println("fuck waiting~~~");
                        Thread.sleep(2500000); // washing for 40 seconds
                    }
                    Thread.sleep(5000);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
