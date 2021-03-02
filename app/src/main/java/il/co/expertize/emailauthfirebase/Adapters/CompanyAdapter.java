package il.co.expertize.emailauthfirebase.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import il.co.expertize.emailauthfirebase.Entities.Travel;
import il.co.expertize.emailauthfirebase.R;
import il.co.expertize.emailauthfirebase.UI.NavigationViewModel;
import il.co.expertize.emailauthfirebase.Util.Gps;

public class CompanyAdapter extends BaseAdapter {
    private Gps gps=new Gps();
    private Context context;
    private ArrayList<Travel> items;
    private Location location;
    private Location location2;
    private NavigationViewModel viewModel;
    Boolean bul=false;
    private int numStatus;
    private FragmentActivity viewModelStore;
    private java.text.SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


    public CompanyAdapter(Context context, ArrayList<Travel> items, FragmentActivity viewModelStore) {
        this.context = context;
        this.items = items;
        this.viewModelStore=viewModelStore;
        viewModel = ViewModelProviders.of(viewModelStore).get(NavigationViewModel.class);
    }

    @Override
    public int getCount() {
        return items.size(); //returns total item in the list

    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns the item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
        
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CompanyAdapter.ViewHolder viewHolder;
        String strDest="";
        String strSrc="";
        location = new Location(LocationManager.GPS_PROVIDER);
        location2 = new Location(LocationManager.GPS_PROVIDER);

        location.setLatitude(items.get(position).getTravelLocation().getLat());
        location.setLongitude(items.get(position).getTravelLocation().getLon());
        strDest =gps.getPlace(location,context);


        location2.setLatitude(items.get(position).getSourceLocation().getLat());
        location2.setLongitude(items.get(position).getSourceLocation().getLon());
        strSrc =gps.getPlace(location2,context);


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_for_company, parent, false);
                viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CompanyAdapter.ViewHolder) convertView.getTag();
        }

        Travel currentItem = (Travel) getItem(position);


        long diffInMillies = Math.abs( currentItem.getTravelDate().getTime() - currentItem.getArrivalDate().getTime());
        long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);





        viewHolder.clientName.setText("name of costumer:    "+currentItem.getClientName());
        viewHolder.clientDestination.setText("destination:      " + strDest);
        viewHolder.clientSource.setText("source:      " + strSrc);
        viewHolder.clientStartDate.setText("start Date:      "+format.format(currentItem.getTravelDate()));
        viewHolder.clientNumOfDay.setText("num of days:      "+diffDays);
        viewHolder.Approve.setEnabled(false);
        String companyName =  viewModel.emailOfUser().split("@")[0];

        bul=false;
        for (String str:currentItem.getCompany().keySet()) {
            String strCurrent = str;
            if (strCurrent.equals(companyName)){
                bul=true;
            }
        }

        viewHolder.Approve.setChecked(false);
      if (bul){

          if (currentItem.getCompany().get(companyName).booleanValue()){
          viewHolder.Approve.setChecked(true);
//          viewHolder.Approve.setTextColor(Color.RED);
//          viewHolder.Approve.setBackgroundColor(Color.parseColor("#F6080F"));
          viewHolder.buttonAccepted.setVisibility(View.GONE);
          viewHolder.buttonCall.setVisibility(View.VISIBLE);
               }
          else {
              viewHolder.buttonCall.setVisibility(View.GONE);
              viewHolder.buttonAccepted.setVisibility(View.VISIBLE);
          }
      }
      else {
          viewHolder.buttonCall.setVisibility(View.GONE);
          viewHolder.buttonAccepted.setVisibility(View.VISIBLE);
      }


        viewHolder.buttonAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(bul)) {
                    currentItem.getCompany().put(companyName,false);
                    viewModel.updateTravel(currentItem);
                    Toast.makeText(context, "The costumer now can see your company", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, "The costumer his already see you", Toast.LENGTH_LONG).show();
            }
        });

        viewHolder.buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ currentItem.getClientPhone()));

                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                viewModelStore.startActivity(callIntent);
            }
        });



        return convertView;
    }

    //ViewHolder inner class
    private class ViewHolder {
        TextView clientName;
        TextView clientDestination;
        TextView clientStartDate;
        TextView clientNumOfDay;
        TextView clientSource;
        CheckBox Approve;
        Button buttonAccepted;
        ImageButton buttonCall;


        public ViewHolder(View view) {
            clientName = (TextView)view.findViewById(R.id.name);
            clientDestination = (TextView) view.findViewById(R.id.dest_travel);
            clientSource = (TextView) view.findViewById(R.id.source_travel);
            clientStartDate = (TextView) view.findViewById(R.id.start_date);
            clientNumOfDay = (TextView) view.findViewById(R.id.num_of_day);
            Approve = (CheckBox) view.findViewById(R.id.checkBox_approve);
            buttonAccepted=(Button) view.findViewById(R.id.button_accept);
            buttonCall=(ImageButton) view.findViewById(R.id.button_call);

        }
    }

}
