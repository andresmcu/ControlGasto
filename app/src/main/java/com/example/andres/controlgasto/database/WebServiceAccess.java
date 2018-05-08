package com.example.andres.controlgasto.database;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.example.andres.controlgasto.HomeActivity;
import com.example.andres.controlgasto.database.model.Expense;
import com.example.andres.controlgasto.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class WebServiceAccess implements DatabaseAccess {

    private static Context mContext;

    public WebServiceAccess(Context context) {
        mContext = context;
    }

    public long insertExpense(Expense expense){

        RESTAccess restaccess = new RESTAccess();
        try {
            // Create URL
            URL expenseURL = new URL(Utils._WS_PROTOCOL, Utils._WS_HOST, Utils._WS_PORT, Utils._WS_FILE + "/" + Utils._USER + "/" + expense.getDate() + "/" + expense.getName() + "/" + expense.getAmount() + "/" + expense.getType() + "/" + expense.getCategories() + "/" + expense.getCurrency());
            restaccess.execute(expenseURL);
        } catch (MalformedURLException e) {
            Log.e(TAG, "WebServiceAccess.insertExpense() MalformedURLException: " + e.getMessage());
        }

        /* Return newly interted row id */
        return 1;
    }

    public List<Expense> getAllExpenses(String user){
        RESTAccess restaccess = new RESTAccess();
        List<Expense> expenses = null;

        try {
            // Create URL
            URL expenseURL = new URL(Utils._WS_PROTOCOL, Utils._WS_HOST, Utils._WS_PORT, Utils._WS_FILE + "/getAll/" + user);
            restaccess.execute(expenseURL);
            expenses = restaccess.get();
        } catch (MalformedURLException e) {
            Log.e(TAG, "WebServiceAccess.insertExpense() MalformedURLException: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "WebServiceAccess.insertExpense() InterruptedException | ExecutionException: " + e.getMessage());
        }

        return expenses;
    }

    /* AsyncTask<Params, Progress, Result> */
    private class RESTAccess extends AsyncTask<URL, Void, List<Expense>> {

        protected void onPreExecute() {
            /* Prepare thask */
        }

        /* Params: URL */
        protected List<Expense> doInBackground(URL... urls) {
            Log.i(TAG, "Entramos en doInBackground()");
            List<Expense> expenses = new ArrayList<>();

            try {
                // Create connection
                HttpURLConnection connection = (HttpURLConnection) urls[0].openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    InputStream responseBody = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"), 8);

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "\n");

                    JsonArray jArray = new JsonParser().parse(sb.toString()).getAsJsonArray();
                    for (int i = 0; i < jArray.size(); i++) {
                        JsonObject jsonObject = jArray.get(i).getAsJsonObject();
                        expenses.add(new Expense(jsonObject.get("id").getAsInt(), jsonObject.get("name").getAsString(), jsonObject.get("date").getAsString(), jsonObject.get("name").getAsString(), jsonObject.get("amount").getAsDouble(), jsonObject.get("type").getAsString(), jsonObject.get("categories").getAsString(), jsonObject.get("currency").getAsString()));
                    }
                } else {
                    Log.i(TAG, "AsyncTask() Error! connection.getResponseCode(): " + connection.getResponseCode());
                }

            } catch (ProtocolException e) {
                Log.e(TAG, "doInBackground() ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "doInBackground() IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "doInBackground() Uncaught exception: " + e.getMessage());
            }

            return expenses;
        }

        /* Result: long */
        protected void onPostExecute(List<Expense> expenses) {
            String text = expenses.size() + " gastos cargados.";
        }

        protected void onCancelled(){
            Log.i(TAG, "onCancelled(). El hilo ha sido cancelado.");
        }
    }

    public int updateExpense(Expense expense) {
        return 1;
    }

    public void deleteExpense (Expense expense){

    }

    public Expense getExpense(long id){
        return null;
    }

    public int getExpensesCount(){
        return 1;
    }
}

