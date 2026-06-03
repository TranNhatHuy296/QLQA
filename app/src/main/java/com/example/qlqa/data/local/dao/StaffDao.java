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

    @Query("SELECT * FROM staff")
    List<Staff> getAllStaff();

    @Query("SELECT * FROM staff WHERE email = :email LIMIT 1")
    Staff getStaffByEmail(String email);

    @Query("SELECT * FROM staff WHERE staffId = :id")
    Staff getStaffById(int id);
}
