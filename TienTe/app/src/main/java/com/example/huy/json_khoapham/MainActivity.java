package com.example.huy.json_khoapham;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView result;
    ListView list;
    EditText amount;
    Button btn;
    Spinner listFrom;
    Spinner listTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = (EditText) findViewById(R.id.amount);
        btn = (Button) findViewById(R.id.confirm);
        list = (ListView) findViewById(R.id.list);
        result = (TextView) findViewById(R.id.result);

        listFrom = (Spinner) findViewById(R.id.listFrom);
        listTo = (Spinner) findViewById(R.id.listTo);
        String[] itemsFrom = new String[]{"USD","AUD","EUR","SGD","THB","GBP","CNY","JPY","KRW"};
        String[] itemsTo = new String[]{"EUR","AUD","SGD","THB","GBP","CNY","JPY","KRW","USD"};
        ArrayAdapter<String> adapterFrom =  new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,itemsFrom);
        ArrayAdapter<String> adapterTo =  new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,itemsTo);
        listFrom.setAdapter(adapterFrom);
        listTo.setAdapter(adapterTo);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute("http://api.fixer.io/latest?base=USD");
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String x = "http://api.fixer.io/latest?base="+from.getText().toString()+"&symbols="+to.getText().toString();
               // Toast.makeText(MainActivity.this,x,Toast.LENGTH_LONG).show();
                new Convert().execute("http://api.fixer.io/latest?base="+listFrom.getSelectedItem().toString()+"&symbols="+listTo.getSelectedItem().toString());
            }
        });
    }
    class ReadJSON extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String chuoi =  docNoiDung_Tu_URL(params[0]);
            return chuoi;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject root = new JSONObject(s);
                String text = root.getString("rates");
                JSONObject newText = new JSONObject(text);
                String AUD = newText.getString("AUD"); // Uc
                String EUR = newText.getString("EUR"); //euro
                String SGD = newText.getString("SGD");//sing
                String THB = newText.getString("THB");//thai
                String GBP = newText.getString("GBP"); // bang anh
                String CNY = newText.getString("CNY"); //china
                String JPY = newText.getString("JPY"); //japan
                String KRW = newText.getString("KRW"); //korea

                ArrayList<String> listCurrency = new ArrayList<String>();
                listCurrency.add("AUD : "+AUD);
                listCurrency.add("EUR : "+EUR);
                listCurrency.add("SGD : "+SGD);
                listCurrency.add("THB : "+THB);
                listCurrency.add("GBP : "+GBP);
                listCurrency.add("CNY : "+CNY);
                listCurrency.add("JPY : "+JPY);
                listCurrency.add("KRW : "+KRW);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        MainActivity.this,android.R.layout.simple_list_item_1,listCurrency
                );
                list.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class Convert extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String chuoi = docNoiDung_Tu_URL(params[0]);
            return chuoi;
        }

        @Override
        protected void onPostExecute(String s) {

            try {

                JSONObject root = new JSONObject(s);
                String son = root.getString("rates");

                JSONObject root2 = new JSONObject(son);
                String tigia = root2.getString(listTo.getSelectedItem().toString());

                double tg = Double.parseDouble(tigia);
                double sl;
                if(amount.getText()!= null) {
                     sl = Double.parseDouble(amount.getText().toString());
                }
                else{
                    sl=0;
                }



                double  kq = tg*sl;
                result.setText(String.valueOf(kq)+" "+listTo.getSelectedItem().toString() );


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private static String docNoiDung_Tu_URL(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        try
        {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return content.toString();
    }
}
