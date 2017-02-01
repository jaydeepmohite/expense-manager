package com.codeitsussie.team85.expensetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {
    final Context context = this;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        sqLiteDatabase = openOrCreateDatabase("ExpenseTracker", MODE_PRIVATE, null);
        Button createButton = (Button) findViewById(R.id.create_budget);
        Button updateButton = (Button) findViewById(R.id.update_budget);
        final Calendar c = Calendar.getInstance();
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        final String month_name = new DateFormatSymbols().getMonths()[month];
        TextView tx = (TextView)findViewById(R.id.budget_header);
        tx.setText("Budget\n("+month_name+"-"+year+")");
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Alert Dialog Code Start*/


                Cursor cursor = sqLiteDatabase.rawQuery("select b_month,b_year,b_amount from budget where b_month = "+month+" and b_year = "+year+";", null);
                if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Create Budget"); //Set Alert dialog title here
                    alert.setMessage("Enter Your Amount"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            String amount = input.getEditableText().toString();
                            Toast.makeText(context, "Budget Created", Toast.LENGTH_LONG).show();
                            sqLiteDatabase.execSQL("insert into budget(b_month,b_year,b_amount,b_cash) values ("+month+","+year+","+amount+",0);");
                        } // End of onClick(DialogInterface dialog, int whichButton)
                    }); //End of alert.setPositiveButton
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
       /* Alert Dialog Code End*/
                }
                else {
                    cursor.moveToFirst();
                    int amount = cursor.getInt(2);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Budget");

                    String month_name = new DateFormatSymbols().getMonths()[month];
                    builder.setMessage("Budget Already Created for " + month_name + " - " + year + " : " + amount + " INR");
                    builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Alert Dialog Code Start*/
                Calendar c = Calendar.getInstance();
                final int month = c.get(Calendar.MONTH);
                final int year = c.get(Calendar.YEAR);
                Cursor cursor = sqLiteDatabase.rawQuery("select b_month,b_year,b_amount from budget where b_month = "+month+" and b_year = "+year+";", null);
                 if(cursor.moveToFirst() || cursor.getCount() > 0){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Update Budget"); //Set Alert dialog title here
                    alert.setMessage("Enter Your Amount"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                     input.setInputType(InputType.TYPE_CLASS_NUMBER);
                     alert.setView(input);

                    alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            String amount = input.getEditableText().toString();
                            Toast.makeText(context, "Budget Created", Toast.LENGTH_LONG).show();
                            sqLiteDatabase.execSQL("update budget set b_amount = "+amount+",b_cash=0 where b_month = "+month+" and b_year = "+year+";");
                        } // End of onClick(DialogInterface dialog, int whichButton)
                    }); //End of alert.setPositiveButton
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
       /* Alert Dialog Code End*/
                }
                else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Budget");
                     String month_name = new DateFormatSymbols().getMonths()[month];
                    builder.setMessage("Budget Not Available for " + month_name + " - " + year + "\nCreate New Budget First");
                    builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
