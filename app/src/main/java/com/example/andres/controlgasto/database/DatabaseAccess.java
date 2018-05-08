package com.example.andres.controlgasto.database;

import com.example.andres.controlgasto.database.model.Expense;

import java.util.List;

public interface DatabaseAccess {
    long insertExpense(Expense expense);
    int updateExpense(Expense expense);
    void deleteExpense (Expense expense);
    Expense getExpense(long id);
    List<Expense> getAllExpenses(String user);
    int getExpensesCount();

}
