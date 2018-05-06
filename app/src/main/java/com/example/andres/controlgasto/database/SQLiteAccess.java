package com.example.andres.controlgasto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.andres.controlgasto.database.model.Expense;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SQLiteAccess extends SQLiteOpenHelper implements DatabaseAccess {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "expenses_db";

    private Context context;

    public SQLiteAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL(Expense.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Expense.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertExpense(Expense expense){
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        values.put(Expense.COLUMN_DATE, expense.getDate());
        values.put(Expense.COLUMN_NAME, expense.getName());
        values.put(Expense.COLUMN_AMOUNT, expense.getAmount());
        values.put(Expense.COLUMN_TYPE, expense.getType());
        values.put(Expense.COLUMN_CATEGORIES, expense.getCategories());
        values.put(Expense.COLUMN_CURRENCY, expense.getCurrency());

        long id = db.insert(Expense.TABLE_NAME, null, values);

        Log.i(TAG, "asdf: " + context.getDatabasePath(DATABASE_NAME));

        /* Close db connection */
        db.close();

        /* Return newly interted row id */
        return id;
    }

    public Expense getExpense(long id){
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        String[] campos = new String[] {"ID", "Date", "Name", "Amount", "Type", "Categories", "Currency"};
        String[] args = new String[] {String.valueOf(id)};

        Expense expensePrueba = new Expense(01, "date","name",05, "type","cat1","euro");
        Expense expense = null;

        Cursor cursor = null;
        try {
            cursor = db.query(Expense.TABLE_NAME,
                    campos,
                    Expense.COLUMN_ID + " = ?",
                    args, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                expense = new Expense(
                        cursor.getInt(cursor.getColumnIndex(Expense.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Expense.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(Expense.COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(Expense.COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(Expense.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(Expense.COLUMN_CATEGORIES)),
                        cursor.getString(cursor.getColumnIndex(Expense.COLUMN_CURRENCY))
                );


            }
        } catch(SQLiteException e) {

        }

        // close the db connection
        cursor.close();

        return expense;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<Expense>();

        /* Select All query */
        String query = "SELECT * FROM " + Expense.TABLE_NAME + " ORDER BY " + Expense.COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(cursor.getInt(cursor.getColumnIndex(Expense.COLUMN_ID)));
                expense.setName(cursor.getString(cursor.getColumnIndex(Expense.COLUMN_NAME)));
                expense.setType(cursor.getString(cursor.getColumnIndex(Expense.COLUMN_TYPE)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(Expense.COLUMN_DATE)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(Expense.COLUMN_AMOUNT)));
                expense.setCurrency(cursor.getString(cursor.getColumnIndex(Expense.COLUMN_CURRENCY)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(Expense.COLUMN_CATEGORIES)));

                expenses.add(expense);
            } while(cursor.moveToNext());
        }

        db.close();

        return expenses;
    }

    public int getExpensesCount() {
        String query = "SELECT  * FROM " + Expense.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String[] campos = new String[] {"Name", "Type", "Amount", "Categories", "Currency"};
        String[] args = new String[] {String.valueOf(expense.getID())};

        values.put(Expense.COLUMN_NAME, expense.getName());
        values.put(Expense.COLUMN_TYPE, expense.getType());
        values.put(Expense.COLUMN_AMOUNT, expense.getAmount());
        values.put(Expense.COLUMN_CATEGORIES, expense.getCategories());
        values.put(Expense.COLUMN_CURRENCY, expense.getCurrency());

        return db.update(Expense.TABLE_NAME, values, Expense.COLUMN_ID + " = ?", args);
    }

    public void deleteExpense (Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Expense.TABLE_NAME, Expense.COLUMN_ID + " = ?", new String[] {String.valueOf(expense.getID())});
        db.close();
    }
}
