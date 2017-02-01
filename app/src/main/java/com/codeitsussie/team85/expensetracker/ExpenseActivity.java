package com.codeitsussie.team85.expensetracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {
    final Context context = this;
    SQLiteDatabase sqLiteDatabase;
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Calendar c = Calendar.getInstance();
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        sqLiteDatabase = openOrCreateDatabase("ExpenseTracker", MODE_PRIVATE, null);
     //   sqLiteDatabase.execSQL("create table if not exists reports(r_id primary key autoincrement");
        sqLiteDatabase.execSQL("create table if not exists category (c_id integer primary key autoincrement, c_name text)");
        sqLiteDatabase.execSQL("create table if not exists expenses (e_id integer primary key autoincrement, budget_id integer,e_category_id integer, e_amount integer, e_mark integer, foreign key(budget_id) references budget(b_id) on delete cascade, foreign key(e_category_id) references category(c_id) on delete cascade);");
        Button add_expense = (Button) findViewById(R.id.add_expense);
        Button update_expense = (Button) findViewById(R.id.update_expense);
        Button delete_expense = (Button) findViewById(R.id.delete_expense);
        Button view_expense = (Button) findViewById(R.id.view_expense);

        Button add_category = (Button) findViewById(R.id.add_category);
        Button delete_category = (Button) findViewById(R.id.delete_category);
        Button view_category = (Button) findViewById(R.id.view_category);
        final String month_name = new DateFormatSymbols().getMonths()[month];
        TextView tx = (TextView)findViewById(R.id.expense_header);
        tx.setText("Expenses\n("+month_name+"-"+year+")");
        Cursor t = sqLiteDatabase.rawQuery("select b_id from budget where b_month="+month+" and b_year="+year+";",null);
        if(!(t.moveToFirst())||t.getCount()==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Budget");
            builder.setMessage("Set Budget First !");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in = new Intent(getApplicationContext(), BudgetActivity.class);
                    startActivity(in);
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String array[] = null;
                Cursor cursor = sqLiteDatabase.rawQuery("select c_name from category",null);
                if((cursor.moveToFirst())){
                    int i = 0;
                    array = new String[cursor.getCount()];
                    do{
                        array[i] = cursor.getString(0);
                        i++;
                    }while(cursor.moveToNext());

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Expense "+month_name+" "+year); //Set Alert dialog title here
                alert.setMessage("Enter Your Expense Details"); //Message here

                // Set an EditText view to get user input
                final Spinner spinner = new Spinner(context);
                    spinner.setBackgroundColor(Color.BLACK);
                final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                final TextView t1 = new TextView(context);
                final TextView t2 = new TextView(context);
                t1.setText("Choose Expense Category :");
                t2.setText("Amount :");
                final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                linear.addView(t1);
                linear.addView(spinner);
                linear.addView(t2);
                linear.addView(input);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                final String[] category = new String[1];
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner.setSelection(position);
                        category[0] = (String) spinner.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                alert.setView(linear);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                       String amount = input.getEditableText().toString();
                       Toast.makeText(context, "Expense Added", Toast.LENGTH_LONG).show();
                       Cursor c1 = sqLiteDatabase.rawQuery("select b_id from budget where b_month="+month+" and b_year="+year+";",null);
                        c1.moveToFirst();
                        int b_id = c1.getInt(0);
                        Cursor c = sqLiteDatabase.rawQuery("select c_id from category where c_name = '"+category[0]+"'", null);
                        c.moveToFirst();
                        int x = c.getInt(0);
                        Cursor c3 = sqLiteDatabase.rawQuery("select e_id from expenses where e_category_id="+x+";",null);
                        if(c3.moveToFirst())
                            sqLiteDatabase.execSQL("update expenses set e_amount = e_amount + "+amount+" where e_category_id = "+x+" and budget_id = "+b_id+";");
                        else
                            sqLiteDatabase.execSQL("insert into expenses(budget_id,e_category_id,e_amount,e_mark) values ("+b_id+","+x+","+amount+",0);");
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Category Added");
                    builder.setMessage("Please Add a Category First!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();

                    alert.show();
                }

            }
        });

        update_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String array[] = null;
                Cursor cursor = sqLiteDatabase.rawQuery("select c_name from category where c_id in (select e_category_id from expenses where budget_id = (select b_id from budget where b_month="+month+" and b_year="+year+") and e_mark=0);",null);
                if((cursor.moveToFirst())){
                    int i = 0;
                    array = new String[cursor.getCount()];
                    do{
                        array[i] = cursor.getString(0);
                        i++;
                    }while(cursor.moveToNext());

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Liabilities"); //Set Alert dialog title here
                    alert.setMessage("Update your Liability Details"); //Message here

                    // Set an EditText view to get user input
                    final Spinner spinner = new Spinner(context);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    final TextView t1 = new TextView(context);
                    final TextView t2 = new TextView(context);
                    t1.setText("Choose Liability Category :");
                    t2.setText("Amount :");
                    final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.addView(t1);
                    linear.addView(spinner);
                    linear.addView(t2);
                    linear.addView(input);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    final String[] category = new String[1];
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinner.setSelection(position);
                            category[0] = (String) spinner.getSelectedItem();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    alert.setView(linear);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            String amount = input.getEditableText().toString();
                            Toast.makeText(context, "Liability Updated", Toast.LENGTH_LONG).show();
                            Cursor c1 = sqLiteDatabase.rawQuery("select e_amount from expenses where e_category_id = (select c_id from category where c_name='" + category[0] + "') and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ");", null);
                            c1.moveToFirst();
                            int amo = c1.getInt(0);
                            //  sqLiteDatabase.execSQL("update budget set ");
                            sqLiteDatabase.execSQL("update budget set b_cash = b_cash + " + amount + " where b_month=" + month + " and b_year=" + year + ";");
                            if ((amo - Integer.parseInt(amount)) == 0) {
                                sqLiteDatabase.execSQL("update expenses set e_mark=1 where  e_category_id = (select c_id from category where c_name='" + category[0] + "') and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ")");
                            } else {

                                sqLiteDatabase.execSQL("update expenses set e_amount = e_amount - " + amount + "  where e_category_id = (select c_id from category where c_name='" + category[0] + "') and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ")");

                            } // End of onClick(DialogInterface dialog, int whichButton)
                        }
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Category Added");
                    builder.setMessage("Please Add a Category First!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();

                    alert.show();
                }


            }
        });

        delete_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String array[] = null;
                Cursor cursor = sqLiteDatabase.rawQuery("select c_name from category  where c_id in (select e_category_id from expenses where budget_id = (select b_id from budget where b_month="+month+" and b_year="+year+"));",null);
                System.out.println(String.valueOf(cursor.getCount()));
                if((cursor.moveToFirst())){
                    int i = 0;
                    array = new String[cursor.getCount()];
                    do{
                        array[i] = cursor.getString(0);
                        i++;
                    }while(cursor.moveToNext());

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Expense"); //Set Alert dialog title here
                    alert.setMessage("Enter Your Expense Details"); //Message here

                    // Set an EditText view to get user input
                    final Spinner spinner = new Spinner(context);
                    final TextView t1 = new TextView(context);

                    t1.setText("Choose Expense Category To Delete :");

                    final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.addView(t1);
                    linear.addView(spinner);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    final String[] cat = new String[1];
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinner.setSelection(position);
                            cat[0] = (String) spinner.getSelectedItem();
                            System.out.println(cat[0]);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    alert.setView(linear);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            System.out.println(cat[0]);
                            Toast.makeText(context, "Expense Deleted", Toast.LENGTH_LONG).show();
                            Cursor c1 = sqLiteDatabase.rawQuery("select b_id from budget where b_month=" + month + " and b_year=" + year + ";", null);
                            c1.moveToFirst();
                            int b_id = c1.getInt(0);
                            Cursor c = sqLiteDatabase.rawQuery("select e_category_id from expenses where budget_id="+b_id+" and e_category_id = (select c_id from category where c_name='"+cat[0]+"');", null);
                            c.moveToLast();
                            int x = c.getInt(0);
                            System.out.println(x);
                            sqLiteDatabase.execSQL("delete from expenses where e_category_id = "+x+";");
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Expenses Available");
                    builder.setMessage("Please Add a Expense First!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();

                    alert.show();
                }

            }
        });


        view_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = sqLiteDatabase.rawQuery("select e_category_id,e_amount,e_mark from expenses where budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ");", null);
                if(c.moveToFirst() || c.getCount() > 0) {
                    int rows = c.getCount();
                    int cols = c.getColumnCount();
                    ScrollView scrollView = new ScrollView(context);
                    scrollView.setVerticalScrollBarEnabled(true);
                    TableLayout table_layout = new TableLayout(context);

                    // outer for loop
                    for (int i = 0; i < rows; i++) {

                        TableRow row = new TableRow(context);
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        // inner for loop
                        for (int j = 0; j < cols; j++) {

                            TextView tv = new TextView(context);
                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextSize(18);
                            tv.setPadding(50, 10, 10, 10);
                            if(j==2)
                                if(c.getInt(2)!=0)
                                    tv.setText("Paid!");
                               else
                                    tv.setText("Pay Later!");
                            else if(j==0) {
                                int y=c.getInt(j);
                                Cursor t = sqLiteDatabase.rawQuery("select c_name from category where c_id="+y+";",null);
                                t.moveToFirst();
                                tv.setText(t.getString(0));
                            }
                            else
                               tv.setText(c.getString(j));
                            row.addView(tv);

                        }

                        c.moveToNext();

                        table_layout.addView(row);

                    }
                    scrollView.addView(table_layout);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Expenses :");
                    builder.setMessage("Expenses for " + month + " - " + year + "\n\nName     Amount     Mark");
                    builder.setView(scrollView);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("No Expenses Available");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Expense Category"); //Set Alert dialog title here
                alert.setMessage("Enter New Category Details"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                final TextView t1 = new TextView(context);
                t1.setText("Enter New Expense Category Name :");
                final LinearLayout linear = new LinearLayout(context);
                linear.setOrientation(LinearLayout.VERTICAL);
                linear.addView(t1);
                linear.addView(input);
                alert.setView(linear);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String cat = input.getEditableText().toString();
                        Cursor c2 = sqLiteDatabase.rawQuery("select c_id from category where c_name='"+cat+"';",null);
                        if(!(c2.moveToFirst()) || c2.getCount()==0) {
                            sqLiteDatabase.execSQL("insert into category(c_name) values('"+cat+"');");
                            Toast.makeText(context, "Category Added", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(context, "Category Aleady Exists!", Toast.LENGTH_LONG).show();
                        }
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
        });

        delete_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = sqLiteDatabase.rawQuery("select c_id from category", null);
                if (c.moveToFirst()) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Expense Category for "+month_name+" "+year); //Set Alert dialog title here
                    alert.setMessage("Enter Category Details"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    final TextView t1 = new TextView(context);
                    t1.setText("Enter Expense Category Name To Delete:");
                    final LinearLayout linear = new LinearLayout(context);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.addView(t1);
                    linear.addView(input);
                    alert.setView(linear);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            String cat1 = input.getEditableText().toString();
                            Cursor c2 = sqLiteDatabase.rawQuery("select c_id from category where c_name='" + cat1 + "';", null);
                            if (!(c2.moveToFirst()) || c2.getCount() == 0) {
                                Toast.makeText(context, "Category Not Exists!", Toast.LENGTH_LONG).show();
                            } else {
                                sqLiteDatabase.execSQL("delete from category where c_name='" + cat1 + "';");
                                Toast.makeText(context, "Category Deleted Successfully!", Toast.LENGTH_LONG).show();
                            }
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Categories Available");
                    builder.setMessage("Please Add a Category First!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();

                    alert.show();
                }
            }
        });

        view_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c3 = sqLiteDatabase.rawQuery("select c_name from category",null);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Categories:");
                if(c3.moveToFirst()) {
                String s="";int i=1;
                do{
                    s = s + "\n\n   •   "+c3.getString(0);
                    i++;
                } while(c3.moveToNext());
                TextView tv = new TextView(context);
                tv.setText(s);

                builder.setView(tv);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                alert.show();
                }
                else {
                    builder.setMessage("No Categories Available");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expense, menu);
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
