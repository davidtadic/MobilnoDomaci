package rs.fon.trecidomaci.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rs.fon.trecidomaci.R;


public class CurrencyFragment extends Fragment {

    private static final String PARAM_SALARY = "salary";
    private static final String API_URL = "http://openexchangerates.org/api/latest.json?app_id=3e6ca2f37522423f8bf30dbb3482d0ff";

    private String salary;
    private TextView eur;
    private TextView gbp;
    private TextView rsd;

    private OnFragmentInteractionListener mListener = null;
    private JSONObject jsonCurrency = null;

    public CurrencyFragment() {
        // Required empty public constructor
    }

    public static CurrencyFragment newInstance(String salary) {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SALARY, salary);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            salary = getArguments().getString(PARAM_SALARY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_currency, container, false);

        TextView usd = (TextView)v.findViewById(R.id.usd);
        eur = (TextView)v.findViewById(R.id.euro);
        gbp = (TextView)v.findViewById(R.id.gbp);
        rsd = (TextView)v.findViewById(R.id.rsd);

        usd.setText(salary+" USD");

        GetCurrencyValues getCurrencyValues = new GetCurrencyValues();
        getCurrencyValues.execute(API_URL);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onRemoveFragment(String salary);
    }

    private class GetCurrencyValues extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... apiURL) {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader reader = null;
            try {
                URL url = new URL(apiURL[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(connection.getInputStream());
                reader = new BufferedReader(new InputStreamReader(in));

                String response = "";

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.e("HTTP OK", "konekcija uspostavljena");
                   while((response = reader.readLine()) != null){
                        stringBuffer.append(response);
                    }

                    jsonCurrency = new JSONObject(stringBuffer.toString());

                    return  jsonCurrency;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Ne_VALJA", "vraca null json");
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                JSONObject rates = jsonObject.getJSONObject("rates");

                    if(rates.has("EUR")){
                        Double eurValue = rates.getDouble("EUR");
                        eur.setText(String.valueOf(Double.valueOf(salary) * eurValue)+ " €");
                    }
                    if(rates.has("GBP")){
                        Double gbpValue = rates.getDouble("GBP");
                        gbp.setText(String.valueOf(Double.valueOf(salary) * gbpValue)+" £");
                    }
                    if(rates.has("RSD")){
                        Double rsdValue = rates.getDouble("RSD");
                        rsd.setText(String.valueOf(Double.valueOf(salary) * rsdValue));
                    }
            } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

