package com.example.azure.washout;

import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements Runnable {

    private String firstData = ""; // tmp for the data that just get from server (eg. Temp_Display,1496721232964,26.1)

    private HashMap<String,ArrayList<String>> allData = new HashMap<String, ArrayList<String>>();

    private View mContentView = null;
    private ImageView freeMachine;
    private ImageView busyMachine ;
    private TextView freeText ;
    private TextView busyText ;
    private FragmentManager fragMgr;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragMgr = getSupportFragmentManager();
        ShowFragment showFrag = new ShowFragment();
        transaction = fragMgr.beginTransaction().add(R.id.frameLayout, showFrag, "Show");
        transaction.commit();
//        freeMachine = (ImageView) findViewById(R.id.freemachine);
//        mContentView = getLayoutInflater().inflate(R.layout.activity_main,null);
//        freeMachine = (ImageView) mContentView.findViewById(R.id.freemachine);
//        busyMachine = (ImageView) mContentView.findViewById(R.id.busymachine);
//        freeText = (TextView) mContentView.findViewById(R.id.free);
//        busyText = (TextView) mContentView.findViewById(R.id.busy);

 //       (new Thread(new MainActivity())).start();


    }

    @Override
    public void run() {
        try {
//            while(true)
//            {
                // wish to get data once every 3 seconds
                sendGet();
                Thread.sleep(3000);
                changeView();
//            }

        }
        catch (SocketException e){
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    // HTTP GET request
    private void sendGet() throws Exception {


//        Socket s = new Socket(InetAddress.getByName("api.mediatek.com"), 80);
//        System.out.println("Socket pass!!!!");
//        PrintWriter pw = new PrintWriter(s.getOutputStream());
//        pw.println("GET /mcs/v2/devices/D2400d9n/connections HTTP/1.1");
//        pw.println("Host: api.mediatek.com");
//        pw.println("deviceKey: qPUnJKNgUWcJyDkG");
//        pw.println("Connection: close");
//        pw.println();
//        pw.flush();
//        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        String t;
//        System.out.println("Wait for Response...");
//        while((t = br.readLine()) != null)
//        {
//            System.out.println("1");
//            System.out.println(t);
//        }
//        br.close();



        String url = "http://api.mediatek.com/mcs/v2/devices/D2400d9n/datachannels/Temp_Display/datapoints.csv";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("deviceKey", "qPUnJKNgUWcJyDkG");
        con.setRequestProperty("Content-Type","text/csv");
        con.setRequestProperty("Connection", "close");


        try {

            InputStream in = con.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                String tmp = String.valueOf(current);
                firstData += tmp;
                data = isw.read();
            }
            parseData(firstData);
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
        ArrayList<String> tmp = new ArrayList<>();

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
            tmp = this.allData.get(name);
            System.out.println("Check value = " + value);
            tmp.add(value);
            this.allData.put(name,tmp);
        }


        for (HashMap.Entry<String, ArrayList<String>> entry : allData.entrySet())
        {
            for(String it : entry.getValue())
            {
                System.out.println("Key = " + entry.getKey() + ", Value = " + it);
            }
        }

//        System.out.println("name: " + name);
//        System.out.println("recordAt: " + recordAt);
//        System.out.println("value: " + value);

    }

    public void changeView()
    {
        // use the channelId directly
        if (allData.get("Temp_Display").size() > 0 )
        {
            this.freeMachine.setVisibility(View.GONE);
//            this.busyMachine.setVisibility(View.VISIBLE);
//            this.freeText.setVisibility(View.GONE);
//            this.busyText.setVisibility(View.VISIBLE);
        }
        else // can compare the data
        {
            int tmp = allData.get("Temp_display").size();

        }
    }


}
