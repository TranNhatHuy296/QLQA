package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert
    long insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM accounts")
    List<Account> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE username = :username LIMIT 1")
    Account getAccountByUsername(String username);

    @Query("SELECT * FROM accounts WHERE accountId = :id")
    Account getAccountById(int id);
}
