package il.co.expertize.emailauthfirebase.Data;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import il.co.expertize.emailauthfirebase.Entities.Travel;

public class TravelFirebaseDataSource implements  ITravelDataSource{

    private static final String TAG = "Firebase";
    //FirebaseUser user;
   // private String a= user.getEmail();
    private MutableLiveData<Boolean> isSuccess= new MutableLiveData<>();
    private List<Travel> allTravelsList;

    private NotifyToTravelListListener notifyToTravelListListener;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference travels = firebaseDatabase.getReference("ExistingTravels");



    private static TravelFirebaseDataSource instance;

    public static TravelFirebaseDataSource getInstance() {
        if (instance == null)
            instance = new TravelFirebaseDataSource();
        return instance;
    }


    private TravelFirebaseDataSource() {
        allTravelsList = new ArrayList<>();
        travels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTravelsList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        if (a == snapshot.getValue(Travel.class).getClientEmail().toString()){
                        Travel travel = snapshot.getValue(Travel.class);
                        allTravelsList.add(travel);
                        //}
                    }
                }
                if (notifyToTravelListListener != null)
                    notifyToTravelListListener.onTravelsChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void setNotifyToTravelListListener(NotifyToTravelListListener l) {
        notifyToTravelListListener = l;
    }


    @Override
    public void addTravel(Travel p) {
        String id = travels.push().getKey();
        p.setTravelId(id);
        travels.child(id).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isSuccess.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccess.setValue(false);
            }
        });
    }


    public  void removeTravel(String id) {
        travels.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Travel Removed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failure removing Travel");
            }
        });
    }


    @Override
    public void updateTravel(final Travel toUpdate) {
//        removeTravel(toUpdate.getTravelId());
//        addTravel(toUpdate);
        travels.child(toUpdate.getTravelId()).setValue(toUpdate);
    }

    @Override
    public List<Travel> getAllTravels() {
        return allTravelsList;
    }

    public MutableLiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }
}
