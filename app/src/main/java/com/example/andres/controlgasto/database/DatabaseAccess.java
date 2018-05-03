package com.example.andres.controlgasto.database;

import com.example.andres.controlgasto.database.model.Expense;

import java.util.List;

public interface DatabaseAccess {
    long insertExpense(String date, String name, Double amount, String type, String categories, String currency);
    int updateExpense(Expense expense);
    void deleteExpense (Expense expense);
    Expense getExpense(long id);
    List<Expense> getAllExpenses();
    int getExpensesCount();

}
