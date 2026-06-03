package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.local.entities.Staff;

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

    @Query("SELECT COUNT(*) FROM accounts")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'Hoạt động' OR status = 'Đang hoạt động'")
    int getActiveCount();

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'Bị khóa' OR status = 'Tạm khóa'")
    int getLockedCount();

    @Transaction
    @Query("SELECT accounts.* FROM accounts JOIN staff ON accounts.staffId = staff.staffId " +
           "WHERE (:search IS NULL OR accounts.username LIKE '%' || :search || '%' OR staff.fullName LIKE '%' || :search || '%' OR accounts.role LIKE '%' || :search || '%') " +
           "AND (CASE WHEN :hasRoles = 1 THEN accounts.role IN (:roles) ELSE 1 END) " +
           "AND (CASE WHEN :hasStatuses = 1 THEN accounts.status IN (:statuses) ELSE 1 END) " +
           "LIMIT :limit OFFSET :offset")
    List<AccountWithStaffResult> getFilteredAccounts(String search, boolean hasRoles, List<String> roles, boolean hasStatuses, List<String> statuses, int limit, int offset);

    @Transaction
    @Query("SELECT COUNT(*) FROM accounts JOIN staff ON accounts.staffId = staff.staffId " +
           "WHERE (:search IS NULL OR accounts.username LIKE '%' || :search || '%' OR staff.fullName LIKE '%' || :search || '%' OR accounts.role LIKE '%' || :search || '%') " +
           "AND (CASE WHEN :hasRoles = 1 THEN accounts.role IN (:roles) ELSE 1 END) " +
           "AND (CASE WHEN :hasStatuses = 1 THEN accounts.status IN (:statuses) ELSE 1 END)")
    int getFilteredCount(String search, boolean hasRoles, List<String> roles, boolean hasStatuses, List<String> statuses);

    class AccountWithStaffResult {
        @Embedded
        public Account account;
        
        @Relation(
            parentColumn = "staffId",
            entityColumn = "staffId"
        )
        public Staff staff;
    }
}
