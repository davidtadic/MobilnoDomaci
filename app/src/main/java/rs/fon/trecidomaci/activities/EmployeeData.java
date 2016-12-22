package rs.fon.trecidomaci.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import rs.fon.trecidomaci.R;
import rs.fon.trecidomaci.database.DatabaseHelper;
import rs.fon.trecidomaci.database.EmployeesContract;
import rs.fon.trecidomaci.fragments.CurrencyFragment;
import rs.fon.trecidomaci.fragments.SMSFragment;
import rs.fon.trecidomaci.model.Employee;

public class EmployeeData extends AppCompatActivity implements SMSFragment.OnFragmentInteractionListener, CurrencyFragment.OnFragmentInteractionListener{
    private TextView idEmployee;
    private TextView name;
    private TextView number;
    private TextView email;
    private TextView position;
    private Button salaryConverter;
    private TextView salary;
    private String id;
    private ImageButton call;
    private ImageButton chat;

    private SharedPreferences sharedPreferences = null;
    private DatabaseHelper dbHelper = null;
    private Employee employee = null;
    private SMSFragment smsFragment = null;
    private CurrencyFragment currencyFragment = null;
    private String tempNumber;
    private String tempName;


    public static final int MY_PERMISSIONS_REQUEST_SMS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);
        getSupportActionBar().setTitle("Info");

        idEmployee = (TextView) findViewById(R.id.id_employee);
        name = (TextView) findViewById(R.id.name_employee);
        number = (TextView) findViewById(R.id.number_employee);
        email = (TextView) findViewById(R.id.email_employee);
        position = (TextView) findViewById(R.id.position_employee);
        salary = (TextView) findViewById(R.id.salary_employee);
        call = (ImageButton) findViewById(R.id.imageButtonCall);
        chat = (ImageButton) findViewById(R.id.imageButtonChat);
        salaryConverter = (Button) findViewById(R.id.button_sallary);

        sharedPreferences = getSharedPreferences("employees", MODE_PRIVATE);
        id = sharedPreferences.getString("id_employee", "");


        dbHelper = new DatabaseHelper(this);
        ReadFromDatabase readFromDatabase = new ReadFromDatabase();
        readFromDatabase.execute(id);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+number.getText().toString()));
                startActivity(i);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendSMS(number.getText().toString(), name.getText().toString());

            }
        });

        salaryConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewCurrencyConverter(salary.getText().toString());
            }
        });

    }

    public void onSendSMS(String mobileNumber, String name){
        smsFragment = SMSFragment.newInstance(mobileNumber, name);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_data, smsFragment);
        fragmentTransaction.commit();
    }

    public void onViewCurrencyConverter(String salary){
        currencyFragment = CurrencyFragment.newInstance(salary);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_data, currencyFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(smsFragment != null && smsFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(smsFragment).commit();
        }else if(currencyFragment != null && currencyFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(currencyFragment).commit();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(tempName != null && !tempName.isEmpty() && tempNumber != null && !tempNumber.isEmpty()) {
                        if(smsFragment != null && smsFragment.isVisible()) {
                            getFragmentManager().beginTransaction().remove(smsFragment).commit();
                        }

                        onSendSMS(tempNumber, tempName);
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onSMSSent(String smsNumber, String smsBody) {
        if(smsFragment != null && smsFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(smsFragment).commit();
        }

        Toast.makeText(getApplicationContext(),"SMS Sent",Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getBaseContext(), ViewEmployees.class));
    }

    @Override
    public void onRemoveFragment(String salary) {
        if(currencyFragment != null && currencyFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(currencyFragment).commit();
        }
        startActivity(new Intent(getBaseContext(), EmployeeData.class));
    }


    private class ReadFromDatabase extends AsyncTask<String, Void, Employee> {


        @Override
        protected Employee doInBackground(String... id) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();


            String[] projection = {EmployeesContract.EmployeesEntry.COLUMN_2_NAME,
                    EmployeesContract.EmployeesEntry.COLUMN_3_NUMBER,
                    EmployeesContract.EmployeesEntry.COLUMN_4_EMAIL,
                    EmployeesContract.EmployeesEntry.COLUMN_5_POSITION,
                    EmployeesContract.EmployeesEntry.COLUMN_6_SALARY
            };
            String name = " ";
            String number = " ";
            String email = " ";
            String position = " ";
            String salary = " ";

            String selection = EmployeesContract.EmployeesEntry._ID + " = ?";
            String[] selectionArgs = {id[0]};

            Cursor c = db.query(EmployeesContract.EmployeesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            while (c.moveToNext()) {
                name = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_2_NAME));
                number = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_3_NUMBER));
                email = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_4_EMAIL));
                position = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_5_POSITION));
                salary = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_6_SALARY));

            }
            employee = new Employee(name, number, email, position, salary);


            c.close();

            return employee;
        }

        @Override
        protected void onPostExecute(Employee employee) {
            idEmployee.setText(id);
            name.setText(employee.getName());
            number.setText(employee.getNumber());
            email.setText(employee.getEmail());
            position.setText(employee.getPosition());
            salary.setText(employee.getSalary());
        }
    }
}
