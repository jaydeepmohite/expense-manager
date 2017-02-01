package com.codeitsussie.team85.expensetracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar c = Calendar.getInstance();
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        final String month_name = new DateFormatSymbols().getMonths()[month];
        sqLiteDatabase = openOrCreateDatabase("ExpenseTracker", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists budget (b_id integer primary key autoincrement, b_month integer, b_year integer, b_amount integer,b_cash integer);");
        TextView tx = (TextView) findViewById(R.id.available_budget);
        TextView tx1 = (TextView) findViewById(R.id.balance);
        String temp = tx.getText().toString();
        temp = temp + "(" + month_name + " - " + year + ") :";
        tx.setText(temp);
        Cursor c1 = sqLiteDatabase.rawQuery("select b_amount, b_cash from budget where b_month  = "+month+" and b_year = "+year+"",null);
        if(c1.moveToFirst()){
            int x = c1.getInt(0);
            int y = c1.getInt(1);
            int z = x - y;
            tx1.setText(String.valueOf(z) + " INR");
        }

        TextView textView = (TextView) findViewById(R.id.summary);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SummaryActivity.class));
            }
        });

        ImageButton budget = (ImageButton) findViewById(R.id.budget);
        ImageButton bills = (ImageButton) findViewById(R.id.bills);
        ImageButton expenses = (ImageButton) findViewById(R.id.expenses);
        ImageButton savings = (ImageButton) findViewById(R.id.savings);
        ImageButton reports = (ImageButton) findViewById(R.id.reports);

        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in1 = new Intent(getApplicationContext(),BudgetActivity.class);
                startActivity(in1);
            }
        });
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2 = new Intent(getApplicationContext(),ResetActivity.class);
                startActivity(in2);
            }
        });
        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in3 = new Intent(getApplicationContext(),ExpenseActivity.class);
                startActivity(in3);
            }
        });
        savings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in4 = new Intent(getApplicationContext(),SavingsActivity.class);
                startActivity(in4);
            }
        });
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in5 = new Intent(getApplicationContext(),ReportsActivity.class);
                startActivity(in5);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
        //super.onBackPressed();
    }
}
