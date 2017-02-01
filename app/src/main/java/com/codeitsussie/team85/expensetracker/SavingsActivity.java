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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class SavingsActivity extends AppCompatActivity {

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);
        final SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("ExpenseTracker",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("create table if not exists savings(s_id integer primary key autoincrement,s_target integer,budget_id integer,foreign key(budget_id) references budget(b_id) on delete cascade);");
        Button saving_target,current_saving,total_saving;
        Calendar c = Calendar.getInstance();
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        final String month_name = new DateFormatSymbols().getMonths()[month];
        TextView tx = (TextView)findViewById(R.id.saving_header);
        tx.setText("Savings\n("+month_name+" "+year+")");
        saving_target = (Button)findViewById(R.id.saving_target);
        current_saving = (Button)findViewById(R.id.current_saving);
        total_saving = (Button)findViewById(R.id.total_saving);
        saving_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c2 = sqLiteDatabase.rawQuery("select s_target from savings where budget_id in (select b_id from budget where b_month=" + month + " and b_year=" + year + ");", null);
                c2.moveToFirst();
                if (!(c2.moveToFirst()) || c2.getCount() == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Savings Target for "+month_name+" "+year); //Set Alert dialog title here
                    alert.setMessage("Set Savings Target"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    final TextView t1 = new TextView(context);
                    t1.setText("Set New Savings Target :");
                    final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.addView(t1);
                    linear.addView(input);
                    alert.setView(linear);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            int target = Integer.parseInt(input.getEditableText().toString());
                            Cursor c4 = sqLiteDatabase.rawQuery("select b_id from budget where b_month=" + month + " and b_year=" + year + ";", null);
                            c4.moveToFirst();
                            int y = c4.getInt(0);
                            sqLiteDatabase.execSQL("insert into savings(s_target,budget_id) values(" + target + "," + y + ");");
                            Toast.makeText(context, "Savings Target Recorded", Toast.LENGTH_LONG).show();
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
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Target Set for "+month_name+" "+year);
                    builder.setMessage("Target Already Set, Please Reset to To Update !");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
        current_saving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = sqLiteDatabase.rawQuery("select s_target from savings where budget_id = (select b_id from budget where b_month = " + month + " and b_year = " + year + ");", null);
                if(c.moveToFirst() || c.getCount() > 0) {
                    int target = c.getInt(0);
                    /*c = sqLiteDatabase.rawQuery("select sum(e_amount) from expenses where e_mark=1 and budget_id = (select b_id from budget where b_month = " + month + " and b_year = " + year + ");", null);
                    c.moveToFirst();
                    int t_expenses = c.getInt(0);*/
                    c = sqLiteDatabase.rawQuery("select b_amount,b_cash from budget where b_month =" + month + " and b_year = " + year + ";", null);
                    c.moveToFirst();
                    int budget = c.getInt(0);
                    int cash = c.getInt(1);
                    int savings = budget - cash;

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Savings for "+month_name+" "+year); //Set Alert dialog title here
                    alert.setMessage("Current Savings"); //Message here

                    // Set an EditText view to get user input
                    final TextView t1 = new TextView(context);
                    final TextView t2 = new TextView(context);
                    t1.setText("Target Savings" + String.valueOf(target) +" INR");
                    t2.setText("Current Savings : " + String.valueOf(savings) +" INR");
                    final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.addView(t1);
                    linear.addView(t2);
                    alert.setView(linear);
                    alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Savings for "+month_name+" "+year);
                    builder.setMessage("Please set a Target first !");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        total_saving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Cursor c=sqLiteDatabase.rawQuery("select sum(b_amount) from budget",null);
               if(c.moveToFirst()){
                int total_b =c.getInt(0);
                c=sqLiteDatabase.rawQuery("select sum(b_cash) from budget",null);
                c.moveToFirst();
                int total_cash =c.getInt(0);
               /* c=sqLiteDatabase.rawQuery("select sum(e_amount) from expenses where e_mark=1",null);
                c.moveToFirst();
                int total_amt =c.getInt(0);*/
                int total_saving=total_b - total_cash;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Total Savings for "+month_name+" "+year);
                builder.setMessage(String.valueOf(total_saving) + " INR");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
               }
                else{
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle("Savings for "+month_name+" "+year);
                   builder.setMessage("No Savings Available!");
                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_savings, menu);
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
