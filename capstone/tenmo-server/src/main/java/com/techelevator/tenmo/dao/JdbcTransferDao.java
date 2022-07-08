package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
    public class JdbcTransferDao implements TransferDao {
        private List<Transfer> transfers = new ArrayList<>();
        private JdbcTemplate jdbcTemplate;

        public JdbcTransferDao(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
        // transfer details
        // list all transfers by transfer_id
        // create transfer boolean
        // optional accept transfer boolean
        // optional reject transfer boolean
        // A transfer includes the User IDs of the from and to users and the amount of TE Bucks.
        // optional pending

        @Override
        public List<Transfer> listOfUsers(){
            List<Transfer> users = new ArrayList<>();
            String sql = "SELECT t.account_to FROM transfer t " +
                    "JOIN account a ON a.account_id = t.account_to;";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);

            while (result.next()){
                users.add(mapRowToTransfer(result));
            }
            return users;
        }
// select user

//    @Override
//    public Transfer selectUserForTransfer(int id) {
//            Transfer transfer = null;
//            String sql =
//            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
//
//            if (result.next()) {
//                transfer = mapRowToTransfer(result);
//            }
//            return transfer;
//    }


    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void create(TransferDTO transferDTO) {

        //insert accountFROM, accountTO, amount to create transfer
        String sqlInsert = " INSERT INTO transfer\n" +
                "\t(account_from, account_to, amount\n" +
                ")\n" +
                "VALUES (?, ?, ?);";
        jdbcTemplate.update(sqlInsert, transferDTO.getToUser(), transferDTO.getFromUser(), transferDTO.getAmount());

        //update accountFROM
        String sqlUpdate1 = "UPDATE account SET balance = balance - ? WHERE account_id = ?;\n";
        jdbcTemplate.update(sqlUpdate1, transferDTO.getAmount());

        //update accountTO
        String sqlUpdate2 = "UPDATE account SET balance = balance + ? WHERE account_id = ?;\n";
        jdbcTemplate.update(sqlUpdate2, transferDTO.getAmount(), transferDTO.getToUser());



        // A transfer includes the User IDs of the from and to users and the amount of TE Bucks.


    }





    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
            Transfer transfer = new Transfer();

            transfer.setTransferId(rowSet.getInt("transfer_id"));
            transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
            transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
            transfer.setAccountFrom(rowSet.getInt("account_from"));
            transfer.setAccountTo(rowSet.getInt("account_to"));
            transfer.setAmount(rowSet.getBigDecimal("amount"));
            return transfer;
        }

    }
