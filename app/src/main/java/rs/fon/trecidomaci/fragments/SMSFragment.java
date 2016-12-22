package rs.fon.trecidomaci.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rs.fon.trecidomaci.R;
import rs.fon.trecidomaci.activities.EmployeeData;


public class SMSFragment extends Fragment {

    private static final String PARAM_NUMBER = "smsNumber";
    private static final String PARAM_NAME = "smsName";
    private String smsNumber;
    private String smsName;
    private OnFragmentInteractionListener mListener = null;

    public SMSFragment() {
        // Required empty public constructor
    }

    public static SMSFragment newInstance(String smsNumber, String smsName) {
        SMSFragment fragment = new SMSFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_NUMBER, smsNumber);
        args.putString(PARAM_NAME, smsName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            smsNumber = getArguments().getString(PARAM_NUMBER);
            smsName = getArguments().getString(PARAM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sms, container, false);

        TextView name = (TextView)v.findViewById(R.id.send_sms_name);
        final EditText message = (EditText)v.findViewById(R.id.send_sms_message);
        Button send = (Button)v.findViewById(R.id.send_button);

        name.setText(smsName);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = smsNumber;
                String messageBody = message.getText().toString();

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if(!(messageBody.isEmpty())) {
                        sendSMS(mobileNumber, messageBody);
                    }else{
                        message.setError("You cannot send empty message!");
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS},
                            EmployeeData.MY_PERMISSIONS_REQUEST_SMS);
                }
            }
        });

        return v;
    }

    public void sendSMS(String mobileNumber, String messageBody){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobileNumber, null,messageBody, null, null);

        mListener.onSMSSent(mobileNumber, messageBody);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= 23) {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onSMSSent(String smsNumber, String smsBody);
    }
}
