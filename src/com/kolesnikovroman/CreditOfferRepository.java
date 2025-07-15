package com.kolesnikovroman;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CreditOfferRepository extends DataBaseRepository {
    /**
     * Возвращает множество уникальных имен банков, кредитные предложения которых уже есть в БД.
     * @return Set с именами существующих банков.
     * @throws SQLException при ошибке доступа к БД.
     */
    public Set<String> getExistingBankNames() throws SQLException {
        Set<String> bankNames = new HashSet<>();
        final String sql = "SELECT DISTINCT bank_name FROM credit_offers;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                bankNames.add(rs.getString("bank_name"));
            }
        }
        return bankNames;
    }

    /**
     * Добавляет одно кредитное предложение в базу данных.
     * @param offer record LoanOfferDTO с данными для вставки.
     * @throws SQLException при ошибке доступа к БД.
     */
    public void add(LoanOfferDTO offer) throws SQLException {
        final String sql = "INSERT INTO credit_offers (bank_name, product_name, amount, rate, term, total_cost) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, offer.bankName());
            statement.setString(2, offer.productName());
            statement.setString(3, offer.amount());
            statement.setString(4, offer.rate());
            statement.setString(5, offer.term());
            statement.setString(6, offer.fullLoanCost());

            statement.executeUpdate();
        }
    }

    /**
     * Получает полный список всех кредитных предложений из базы данных для проверки.
     * @return Список объектов LoanOfferDTO.
     */
    public List<LoanOfferDTO> findAll() throws SQLException {
        List<LoanOfferDTO> offers = new ArrayList<>();
        final String sql = "SELECT bank_name, product_name, amount, rate, term, total_cost FROM credit_offers ORDER BY bank_name;";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                offers.add(new LoanOfferDTO(
                        rs.getString("bank_name"),
                        rs.getString("product_name"),
                        rs.getString("amount"),
                        rs.getString("rate"),
                        rs.getString("term"),
                        rs.getString("total_cost")
                ));
            }
        }
        return offers;
    }
}