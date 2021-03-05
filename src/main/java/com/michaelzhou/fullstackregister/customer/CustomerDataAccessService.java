package com.michaelzhou.fullstackregister.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// Data Access Layer
@Repository
public class CustomerDataAccessService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<Customer> selectAllCustomers() {
        String sql = "" +
                "SELECT " +
                " customer_id, " +
                " first_name, " +
                " last_name,  " +
                " email ," +
                " gender " +
                "FROM customer";
        // Grabs the raw data from the database and transforms it into a Java object (i.e. Customer object)
        return jdbcTemplate.query(sql, mapCustomerFromDb());
    }

    int insertCustomer(UUID customerId, Customer customer) {
        String sql = "" +
                "INSERT INTO customer (" +
                " customer_id, " +
                " first_name, " +
                " last_name, " +
                " email, " +
                " gender) " +
                "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                sql,
                customerId,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getGender().name().toUpperCase()
        );
    }

    private RowMapper<Customer> mapCustomerFromDb() {
        return (resultSet, i) -> {
            String customerIdStr = resultSet.getString("customer_id");
            UUID customerId = UUID.fromString(customerIdStr);

            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String email = resultSet.getString("email");

            String genderStr = resultSet.getString("gender");
            Customer.Gender gender = Customer.Gender.valueOf(genderStr);

            return new Customer(
                    customerId, firstName, lastName, email, gender
            );
        };
    }

    int updateEmail(UUID customerId, String email) {
        String sql = "" +
                "UPDATE customer " +
                "SET email = ? " +
                "WHERE customer_id = ?";
        return jdbcTemplate.update(sql, email, customerId);
    }

    int updateFirstName(UUID customerId, String firstName) {
        String sql = "" +
                "UPDATE customer " +
                "SET first_name = ? " +
                "WHERE customer_id = ?";
        return jdbcTemplate.update(sql, firstName, customerId);
    }

    int updateLastName(UUID customerId, String lastName) {
        String sql = "" +
                "UPDATE customer " +
                "SET last_name = ? " +
                "WHERE customer_id = ?";
        return jdbcTemplate.update(sql, lastName, customerId);
    }

    @SuppressWarnings("ConstantConditions")
    boolean isEmailTaken(String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                " SELECT 1 " +
                " FROM customer " +
                " WHERE email = ?" +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[] {email},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    @SuppressWarnings("ConstantConditions")
    public boolean selectExistsEmail(UUID customerId, String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                " SELECT 1 " +
                " FROM customer " +
                " WHERE customer_id <> ? " +
                " AND email = ? " +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{customerId, email},
                ((resultSet, columnIndex) -> resultSet.getBoolean(1))
        );
    }

    int deleteCustomerById(UUID customerId) {
        String sql = "" +
                "DELETE FROM customer " +
                "WHERE customer_id = ?";
        return jdbcTemplate.update(sql, customerId);
    }
}
