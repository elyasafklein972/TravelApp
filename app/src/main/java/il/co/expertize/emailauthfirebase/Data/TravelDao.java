package il.co.expertize.emailauthfirebase.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.co.expertize.emailauthfirebase.Entities.Travel;

@Dao
public interface TravelDao {

    @Query("select * from travels")
    LiveData<List<Travel>> getAll();

    @Query("select * from travels where travelId=:id")
    LiveData<Travel> get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Travel travel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Travel> travels);

    @Update
    void update(Travel travel);

    @Delete
    void delete(Travel... travels);

    @Query("delete from travels")
    void clear();

}
