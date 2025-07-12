package com.sushkomihail.datastructures;

import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.IncomeDTO;

import java.util.ArrayList;
import java.util.List;

public class TransactionsCollection {
    List<IncomeDTO> incomes = new ArrayList<>();
    List<ExpenseDTO> expenses = new ArrayList<>();

    public List<IncomeDTO> getIncomes() {
        return incomes;
    }

    public List<ExpenseDTO> getExpenses() {
        return expenses;
    }

    public void addIncome(IncomeDTO income) {
        incomes.add(income);
    }

    public void addExpense(ExpenseDTO expense) {
        expenses.add(expense);
    }
}
