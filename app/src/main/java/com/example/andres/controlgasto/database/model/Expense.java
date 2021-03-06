package com.example.andres.controlgasto.database.model;

import java.util.List;

public class Expense {
    public static final String TABLE_NAME = "Expenses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_CURRENCY = "currency";

    private int ID;
    private String User;
    private String Date;
    private String Name;
    private double Amount;
    private String Type;
    private String Categories;
    private String Currency;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER + "TEXT, "
                    + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_AMOUNT + " REAL, "
                    + COLUMN_TYPE + " TEXT, "
                    + COLUMN_CATEGORIES + " TEXT, "
                    + COLUMN_CURRENCY + " TEXT"
                    + ")";

    public Expense() {}

    public Expense(int ID, String User, String Date, String Name, double Amount, String Type, String Categories, String Currency) {
        this.ID = ID;
        this.User = User;
        this.Date = Date;
        this.Name = Name;
        this.Amount = Amount;
        this.Type = Type;
        this.Categories = Categories;
        this.Currency = Currency;
    }

    public Expense(String User, String Date, String Name, double Amount, String Type, String Categories, String Currency) {
        this.User = User;
        this.Date = Date;
        this.Name = Name;
        this.Amount = Amount;
        this.Type = Type;
        this.Categories = Categories;
        this.Currency = Currency;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUser() { return User; }

    public void setUser(String User) { this.User = User; }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCategories() {
        return Categories;
    }

    public void setCategory(String categories) {
        Categories = categories;
    }
}
