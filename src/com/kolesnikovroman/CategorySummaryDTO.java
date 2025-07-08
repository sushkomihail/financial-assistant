package com.kolesnikovroman;
import java.math.BigDecimal;

public class CategorySummaryDTO {
    private final String categoryName;
    private final BigDecimal totalAmount;

    public CategorySummaryDTO(String categoryName, BigDecimal totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // Необязательно, но полезно для отладки
    @Override
    public String toString() {
        return "CategorySummaryDTO{" +
                "categoryName='" + categoryName + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}