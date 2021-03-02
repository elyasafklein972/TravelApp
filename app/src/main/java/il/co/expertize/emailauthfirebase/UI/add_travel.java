package il.co.expertize.emailauthfirebase.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import il.co.expertize.emailauthfirebase.Data.UserLocation;
import il.co.expertize.emailauthfirebase.Entities.Travel;
import il.co.expertize.emailauthfirebase.R;

public class add_travel extends Fragment  {
    Context context;
    UserLocation user1;
    private NavigationViewModel travelViewModel;
    Location travelLocation;
    private Travel travel;
    private EditText name;
    private EditText phone;
    private EditText email;
    private EditText des;
    private EditText source;
    private String dstAddress;
    private String srcAddress;
    private Spinner status;
    private Date start;
    private Date End;
    Button dateStart;
    Button dateStop;
    Button goTravel;
    Calendar c1 = Calendar.getInstance();
    Calendar c2 = Calendar.getInstance();
    DatePickerDialog d1;
    DatePickerDialog d2;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_travel, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user1=new UserLocation();
        travelViewModel = ViewModelProviders.of(getActivity()).get(NavigationViewModel.class);
        name= view.findViewById(R.id.name);
        phone=view.findViewById(R.id.phone);
        email=view.findViewById(R.id.email);
        des= view.findViewById(R.id.let);
        source= view.findViewById(R.id.source);
        goTravel=(Button)view.findViewById(R.id.go_travel);
        dateStart=(Button)view.findViewById(R.id.date_source);
        dateStop=(Button)view.findViewById(R.id.date_destination);
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1=Calendar.getInstance();
                int day =c1.get(Calendar.DAY_OF_MONTH);
                int month = c1.get(Calendar.MONTH);
                int year = c1.get(Calendar.YEAR);
                d1= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        dateStart.setText(mDay+"/"+(mMonth+1)+"/"+(mYear));
                        String date = mYear+"-"+(mMonth+1)+"-"+mDay;
                        start=new Travel.DateConverter().fromTimestamp(date);
                    }
                }
                        ,year,month,day);
                d1.show();
            }
        });
        dateStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c2=Calendar.getInstance();
                int day =c2.get(Calendar.DAY_OF_MONTH);
                int month = c2.get(Calendar.MONTH);
                int year = c2.get(Calendar.YEAR);
                d2= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        dateStop.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                        String date = mYear+"-"+(mMonth+1)+"-"+mDay;
                        End=new Travel.DateConverter().fromTimestamp(date);
                    }
                }
                        ,year,month,day);
                d2.show();
            }
        });




        goTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validEmail() | !validDes() | !validName() | !validPhone()){
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context);
                builder.setTitle("Hello "+ name.getText().toString());
                builder.setMessage("Are you sure you want this trip?");
                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(context,"No is clicked",Toast.LENGTH_LONG).show();
                            }
                        });

                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(context, "Yes is clicked", Toast.LENGTH_LONG).show();
                                dstAddress = des.getText().toString().trim();
                                srcAddress = source.getText().toString().trim();
                                travel= new Travel();
                                travel.setClientName(name.getText().toString().trim());
                                travel.setClientPhone(phone.getText().toString().trim());
                                travel.setClientEmail(email.getText().toString().trim());
                                travel.setTravelDate(start);
                                travel.setArrivalDate(End);
                                travel.setCompany(new HashMap<String, Boolean>());
                                travel.setRequesType(Travel.RequestType.sent);
                                travel.setTravelLocation(user1.convertFromLocation(submitted(dstAddress)));
                                travel.setSourceLocation(user1.convertFromLocation(submitted(srcAddress)));
                                travel.getCompany().put("Afikim",Boolean.FALSE);
                                travel.getCompany().put("SuperBus",Boolean.FALSE);
                                travel.getCompany().put("SmartBus",Boolean.FALSE);
                                travel.getCompany().put("elyasaf007",Boolean.FALSE);
                                travel.getCompany().put("omer",Boolean.TRUE);
                                travelViewModel.addTravel(travel);
                                name.getText().clear();
                                phone.getText().clear();
                                email.getText().clear();
                                des.getText().clear();
                                source.getText().clear();


                            }
                        });
                builder.setNeutralButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(context,"Cancel is clicked",Toast.LENGTH_LONG).show();
                            }
                        });

                builder.show();

            }
        });
    }


    public Location submitted(String travelAddress){
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> l = geocoder.getFromLocationName(travelAddress, 1);

            if (!l.isEmpty()) {
                Address temp = l.get(0);
                travelLocation = new Location("travelLocation");
                travelLocation.setLatitude(temp.getLatitude());
                travelLocation.setLongitude(temp.getLongitude());
                return  travelLocation;
            } else {
                Toast.makeText(context, "4:Unable to understand address", Toast.LENGTH_LONG).show();
                des.setError("4:Unable to understand address");

                return null;

            }
        } catch (IOException e) {
            Toast.makeText(context, "5:Unable to understand address. Check Internet connection.", Toast.LENGTH_LONG).show();
            des.setError("5:Unable to understand address. Check Internet connection.");
            return null;
        }
    }

//    /**
//     * add the pickupp destanation address from the edit text to the user location list and empty the edit text
//     * @param view
//     */
//    public void addAddress(View view) {
//        try {
//
//
//            if (des.getEditText().getText().toString().isEmpty())
//                Toast.makeText(this, "enter a destination address", Toast.LENGTH_LONG).show();
//            else {
//                travelAddress = des.getEditText().getText().toString().trim();
//                destAddressArr.add(user.convertFromLocation(submitted()));
//
//                des.getEditText().getText().clear();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//


    private boolean validEmail(){
        String emailInput=email.getText().toString().trim();
        if (emailInput.isEmpty()){
            email.setError("Field can't be empty");
            return false;
        }else {
            email.setError(null);
            return true;
        }
    }


    private boolean validName(){
        String nameInput=name.getText().toString().trim();
        if (nameInput.isEmpty()){
            name.setError("Field can't be empty");
            return false;
        }else {
            name.setError(null);
            return true;
        }
    }

    private boolean validDes(){
        String lonInput=des.getText().toString().trim();
        if (lonInput.isEmpty()){
            des.setError("Field can't be empty");
            return false;
        }else {
            des.setError(null);
            return true;
        }
    }


    private boolean validPhone(){
        String phoneInput=phone.getText().toString().trim();
        if (phoneInput.isEmpty()){
            phone.setError("Field can't be empty");
            return false;
        }else
        if(phoneInput.length()>10) {
            phone.setError("phone too long");
            return false;
        }
        else if(phoneInput.length()<10) {
            phone.setError("phone too short");
            return false;
        }else
        {
            phone.setError(null);
            return true;
        }
    }
}