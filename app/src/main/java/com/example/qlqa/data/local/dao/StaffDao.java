package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Staff;

import java.util.List;

@Dao
public interface StaffDao {
    @Insert
    long insert(Staff staff);

    @Update
    void update(Staff staff);

    @Delete
    void delete(Staff staff);

    @Query("SELECT * FROM staff ORDER BY employeeCode ASC")
    List<Staff> getAllStaff();

    @Query("SELECT * FROM staff WHERE email = :email LIMIT 1")
    Staff getStaffByEmail(String email);

    @Query("SELECT * FROM staff WHERE staffId = :id")
    Staff getStaffById(int id);

    @Query("SELECT * FROM staff WHERE employeeCode = :code LIMIT 1")
    Staff getStaffByCode(String code);

    @Query("SELECT COUNT(*) FROM staff")
    int getTotalStaffCount();

    @Query("SELECT COUNT(*) FROM staff WHERE status = 'Đã có mặt'")
    int getPresentStaffCount();

    @Query("SELECT * FROM staff WHERE " +
           "(:search IS NULL OR fullName LIKE '%' || :search || '%' OR employeeCode LIKE '%' || :search || '%') " +
           "AND (:position IS NULL OR position = :position) " +
           "ORDER BY employeeCode ASC")
    List<Staff> getFilteredStaff(String search, String position);

    @Query("SELECT MAX(staffId) FROM staff")
    Integer getMaxStaffId();
}
