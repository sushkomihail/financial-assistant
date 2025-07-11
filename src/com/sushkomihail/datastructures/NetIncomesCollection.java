package com.sushkomihail.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Структура, хранящая чистый доход по месяцам за определенный период
 */
public class NetIncomesCollection {
    private final List<Integer> netIncomes = new ArrayList<>();

    public NetIncomesCollection(List<Integer> incomes, List<Integer> expenses) {
        init(incomes, expenses);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < netIncomes.size() - 1; i++) {
            string.append(netIncomes.get(i)).append(", ");
        }

        string.append(netIncomes.get(netIncomes.size() - 1));
        return string.toString();
    }

    private void init(List<Integer> incomes, List<Integer> expenses) {
        if (incomes.size() != expenses.size()) {
            System.err.println("[ERROR][class NetIncomesCollection]: Массивы доходов и расходов имеют разную длину.");
            return;
        }

        for (int i = 0; i < incomes.size(); i++) {
            int netIncome = incomes.get(i) - expenses.get(i);
            netIncomes.add(netIncome);
        }
    }
}
