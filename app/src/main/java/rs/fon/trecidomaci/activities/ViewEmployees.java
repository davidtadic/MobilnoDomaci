package rs.fon.trecidomaci.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import rs.fon.trecidomaci.R;
import rs.fon.trecidomaci.database.DatabaseHelper;
import rs.fon.trecidomaci.database.EmployeesContract;

public class ViewEmployees extends AppCompatActivity {

    private ListView listView;
    private EditText editTextId;
    private Button findButton;

    private ArrayList<String> employeeList;
    private ArrayAdapter<String> employeeAdapter = null;
    private DatabaseHelper dbHelper = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);
        getSupportActionBar().setTitle("Preview");

        employeeList = new ArrayList<>();
        employeeAdapter = new ArrayAdapter<String>(this,R.layout.employee_item,employeeList);

        listView = (ListView)findViewById(R.id.list_view);
        editTextId = (EditText)findViewById(R.id.id_employee_input);
        findButton = (Button)findViewById(R.id.button_view);

        sharedPreferences = getSharedPreferences("employees",MODE_PRIVATE);


        registerForContextMenu(listView);
        listView.setAdapter(employeeAdapter);

        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.add_floating);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewEmployees.this, AddEmployees.class));
            }
        });




        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String idEmployee = editTextId.getText().toString();
                if(!(idEmployee.isEmpty())) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id_employee", idEmployee);
                    editor.apply();
                    startActivity(new Intent(ViewEmployees.this, EmployeeData.class));
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHelper = new DatabaseHelper(this);

        ReadFromDatabase readFromDatabase = new ReadFromDatabase();
        readFromDatabase.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.list_view){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_item, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String selected = employeeList.get(info.position);
        String id = "";
        if(selected.contains(" ")){
            id = selected.substring(0,selected.indexOf(" "));
        }

        if(item.getItemId() == R.id.delete_id){
            int deletedRows = onDeleteEmployee(id);
            if(deletedRows > 0){
                employeeList.remove(info.position);
                employeeAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Employee has been deleted", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(), "Employee has NOT been deleted", Toast.LENGTH_LONG).show();

            }
        }

        return true;
    }

    public int onDeleteEmployee(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return  db.delete(EmployeesContract.EmployeesEntry.TABLE_NAME, EmployeesContract.EmployeesEntry._ID + " = ?", new String[] {id});
    }


    private class ReadFromDatabase extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            ArrayList<String> employeeList = new ArrayList<>();

            String[] projection = {EmployeesContract.EmployeesEntry._ID, EmployeesContract.EmployeesEntry.COLUMN_2_NAME};

            Cursor c = db.query(EmployeesContract.EmployeesEntry.TABLE_NAME, projection, null, null, null, null, null);

            while(c.moveToNext()){
                int employeeId = c.getInt(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry._ID));
                String employeeName = c.getString(c.getColumnIndexOrThrow(EmployeesContract.EmployeesEntry.COLUMN_2_NAME));

                String employeeData = String.valueOf(employeeId)+" : "+employeeName;

                employeeList.add(employeeData);
            }

            c.close();

            return employeeList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            if(strings != null){
                employeeList.clear();
                employeeList.addAll(strings);

                employeeAdapter.notifyDataSetChanged();
                Log.d("dbOperation", "read success");
            }else{
                Log.d("dbOperation", "nothing to read");
            }
        }
    }
}
