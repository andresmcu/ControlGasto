package com.example.andres.controlgasto.database;

import com.example.andres.controlgasto.database.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class WebServiceAccess implements DatabaseAccess {

    public long insertExpense(String date, String name, Double amount, String type, String categories, String currency){


        /* Return newly interted row id */
        return 1;
    }

    public int updateExpense(Expense expense) {
        return 1;
    }

    public void deleteExpense (Expense expense){

    }

    public Expense getExpense(long id){
        return null;
    }

    public List<Expense> getAllExpenses(){
        return new ArrayList<>();
    }

    public int getExpensesCount(){
        return 1;
    }
}
