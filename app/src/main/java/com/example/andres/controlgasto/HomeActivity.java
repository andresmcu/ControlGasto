package com.example.andres.controlgasto;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.controlgasto.database.DatabaseAccess;
import com.example.andres.controlgasto.database.WebServiceAccess;
import com.example.andres.controlgasto.database.model.Expense;
import com.example.andres.controlgasto.utils.MyDividerItemDecoration;
import com.example.andres.controlgasto.utils.RecyclerTouchListener;
import com.example.andres.controlgasto.utils.Utils;
import com.example.andres.controlgasto.view.ExpensesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ExpensesAdapter mAdapter;
    private List<Expense> expensesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_expenses_view);

        // db = new SQLiteAccess(this);
        db = new WebServiceAccess(this);
        List<Expense> expenses = db.getAllExpenses(Utils._USER);
        if (expenses != null)
            Log.i("HomeActivity", expenses.size() + " gastos recuperados en UI.");
        else
            Log.i("HomeActivity", "La lista expenses está nula.");

        expensesList.addAll(expenses);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExpenseDialog(false, null, -1);
            }
        });

        mAdapter = new ExpensesAdapter(this, expensesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyExpenses();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                openExpenseDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new expense in db
     * and refreshing the list
     */
    private void createExpense(Expense expense) {
        DateFormat df = new DateFormat();
        String date = df.format("yyyy-MM-dd HH:mm:ss", new Date()).toString();

        expense.setDate(date);
        long id = db.insertExpense(expense);

        Expense n = db.getExpense(id);


        if (n != null) {
            // adding new note to array list at 0 position
            expensesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyExpenses();
        }

    }

    /**
     * Updating expense in db and updating
     * item in the list by its position
     */
    private void updateNote(String name, int position) {
        Expense expense = expensesList.get(position);
        // updating note text
        expense.setName(name);

        // updating note in db
        db.updateExpense(expense);



        // refreshing the list
        expensesList.set(position, expense);
        mAdapter.notifyItemChanged(position);

        toggleEmptyExpenses();
    }

    /**
     * Deleting expense from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteExpense(expensesList.get(position));

        // removing the note from the list
        expensesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyExpenses();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showExpenseDialog(true, expensesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void openExpenseDialog(final int position) {
        Expense expense = expensesList.get(position);
        Toast.makeText(HomeActivity.this, expense.getName() + " - " + expense.getID(), Toast.LENGTH_SHORT).show();
    }

    private void showExpenseDialog(final boolean shouldUpdate, final Expense expense, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.expense_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilderUserInput.setView(view);


        final EditText inputExpenseName = view.findViewById(R.id.expense_name);
        final EditText inputExpenseAmount = view.findViewById(R.id.expense_amount);
        final EditText inputExpenseType = view.findViewById(R.id.expense_type);
        final EditText inputExpenseCategories = view.findViewById(R.id.expense_categories);
        final EditText inputExpenseCurrency = view.findViewById(R.id.expense_currency);

        TextView dialogTitle = view.findViewById(R.id.expense_name_dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_expense_title) : getString(R.string.lbl_edit_expense_title));

        if (shouldUpdate && expense != null) {
            inputExpenseName.setText(expense.getName());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputExpenseName.getText().toString())) {
                    Toast.makeText(HomeActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && expense != null) {
                    // update note by it's id
                    updateNote(inputExpenseName.getText().toString(), position);
                } else {
                    double inputExpenseAmountD;
                    String inputExpenseDate;

                    try {
                        inputExpenseAmountD = Double.parseDouble(inputExpenseAmount.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(HomeActivity.this, "La cantidad debe ser un valor numérico. Para decimales, utilice el punto (.).", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String currentDate = sdf.format(new Date());
                    inputExpenseDate = currentDate;

                    Expense expense = new Expense(Utils._USER, inputExpenseDate, inputExpenseName.getText().toString(), inputExpenseAmountD, inputExpenseType.getText().toString(), inputExpenseCategories.getText().toString(), inputExpenseCurrency.getText().toString());
                    createExpense(expense);
                }
            }
        });
    }

    /**
     * Toggling list and empty expenses view
     */
    private void toggleEmptyExpenses() {
        // you can check notesList.size() > 0

        if (db.getExpensesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
