package il.co.expertize.emailauthfirebase.UI;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import il.co.expertize.emailauthfirebase.Data.ITravelDataSource;
import il.co.expertize.emailauthfirebase.Data.TravelFirebaseDataSource;
import il.co.expertize.emailauthfirebase.Entities.Travel;

public class myService extends Service {
    Integer sum = 0;
    static Integer numOfTravel = 100000000;
    static Integer countOfTravel = 0;
    ITravelDataSource travelDataSource;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference travels = firebaseDatabase.getReference("ExistingTravels");
    boolean isThreadOn = false;
    public final String TAG = "myService";

    @Override
    public void onCreate() {
        super.onCreate();
        travelDataSource = TravelFirebaseDataSource.getInstance();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        travels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countOfTravel=0;
                Intent intent1=new Intent("Add_travel");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        countOfTravel++;
                    }
                    if (countOfTravel>numOfTravel){
                        intent1.setAction("com.javacodegeeks.android.A_NEW_TRAVEL");
                        sendBroadcast(intent1);
                        numOfTravel=countOfTravel;
                        countOfTravel = 0;
                    }
                    numOfTravel=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!isThreadOn)
            Toast.makeText(this,"thanks of using travel app", Toast.LENGTH_LONG).show();
        sum=0;
        Log.d(TAG," onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public class SumCalc extends Thread {

        public void run() {
            sum = 0;
            for(Integer idx = 0; idx< 10099; idx ++)
            {
                sum++;
            }
            isThreadOn = false;
        }
    }

}
