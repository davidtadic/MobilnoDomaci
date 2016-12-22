package rs.fon.trecidomaci.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import rs.fon.trecidomaci.R;
import rs.fon.trecidomaci.database.DatabaseHelper;
import rs.fon.trecidomaci.database.EmployeesContract;
import rs.fon.trecidomaci.model.Employee;

public class AddEmployees extends AppCompatActivity {
    private EditText name;
    private EditText number;
    private EditText email;
    private EditText position;
    private EditText salary;
    private Button submit;
    private DatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employees);
        getSupportActionBar().setTitle("Add");

        name = (EditText)findViewById(R.id.input_name);
        number = (EditText)findViewById(R.id.input_number);
        email = (EditText)findViewById(R.id.input_email);
        position = (EditText)findViewById(R.id.input_position);
        salary = (EditText)findViewById(R.id.input_sallary);
        submit = (Button)findViewById(R.id.button_enter_employee);

        dbHelper = new DatabaseHelper(this);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                Employee employee = new Employee(name.getText().toString(), number.getText().toString(),email.getText().toString(),position.getText().toString(),salary.getText().toString());
                WriteToDataBase writeToDataBase = new WriteToDataBase();
                writeToDataBase.execute(employee);

                finish();
                    startActivity(new Intent(AddEmployees.this, MainActivity.class));
                }
            }
        });

    }

    public boolean isValid() {
        boolean valid = true;

        String nameV = name.getText().toString();
        String numberV = number.getText().toString();
        String emailV = email.getText().toString();
        String positionV = position.getText().toString();
        String salaryV = salary.getText().toString();
        String regex = "0-9";

        if (nameV.isEmpty() || nameV.length() < 2) {
            name.setError("at least 2 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (numberV.isEmpty() || numberV.length() < 9) {
            number.setError("enter correct number");
            valid = false;
        } else {
            number.setError(null);
        }

        if (emailV.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailV).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (positionV.isEmpty()) {
            position.setError("enter a position");
            valid = false;
        } else {
            position.setError(null);
        }

        if (Pattern.matches(regex,salaryV) || salaryV.isEmpty()) {
            salary.setError("insert number");
            valid = false;
        } else {
            salary.setError(null);
        }

        return valid;
    }


    public class WriteToDataBase extends AsyncTask<Employee, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Employee... employees) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(EmployeesContract.EmployeesEntry.COLUMN_2_NAME, employees[0].getName());
            values.put(EmployeesContract.EmployeesEntry.COLUMN_3_NUMBER, employees[0].getNumber());
            values.put(EmployeesContract.EmployeesEntry.COLUMN_4_EMAIL, employees[0].getEmail());
            values.put(EmployeesContract.EmployeesEntry.COLUMN_5_POSITION, employees[0].getPosition());
            values.put(EmployeesContract.EmployeesEntry.COLUMN_6_SALARY, employees[0].getSalary());

            long result = db.insert(EmployeesContract.EmployeesEntry.TABLE_NAME, null, values);

            if(result == -1){
                return false;
            }else{
                return true;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (!aBoolean) {
                Toast.makeText(getBaseContext(), "Employee has NOT been inserted", Toast.LENGTH_LONG).show();
            } else {
                Log.d("dbOperation", "insert success");
                Toast.makeText(getBaseContext(), "Employee has been inserted", Toast.LENGTH_LONG).show();
            }
        }
    }
}
