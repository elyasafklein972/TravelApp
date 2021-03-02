package il.co.expertize.emailauthfirebase.UI;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import il.co.expertize.emailauthfirebase.Adapters.CustomListAdapter;
import il.co.expertize.emailauthfirebase.Adapters.HistoryAdapter;
import il.co.expertize.emailauthfirebase.Entities.Travel;
import il.co.expertize.emailauthfirebase.R;

public class HistoryTravelsFragment extends Fragment {

    NavigationViewModel mViewModel;
    Context context;
    ListView itemsListView;
    Date start;
    Date end;



    public static HistoryTravelsFragment newInstance() {
        return new HistoryTravelsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.history_travels_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemsListView  = (ListView)view.findViewById(R.id.list_history);

        String travelDate ;
                travelDate =  "2020"+"-"+"1"+"-"+"1";
                 String  travelDate2 =  "2022"+"-"+"12"+"-"+"12";
        try {
            start = new Travel.DateConverter().fromTimestamp(travelDate);
            end = new Travel.DateConverter().fromTimestamp(travelDate2);
            if (start == null)
                throw new Exception("שגיאה בתאריך");
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }



        mViewModel = ViewModelProviders.of(getActivity()).get(NavigationViewModel.class);

        mViewModel.getAllCloseTravelList().observe(this, new Observer<List<Travel>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<Travel> travels) {
                ArrayList<Travel> tmp = new ArrayList<Travel>(travels);
                ArrayList<Travel> res=getAllCloseTravelList(start,end,tmp);
                //create adapter object
                HistoryAdapter adapter = new HistoryAdapter(context, res,requireActivity());

                //set custom adapter as adapter to our list view
                itemsListView.setAdapter(adapter);
            }});
    }

    public  ArrayList<Travel> getAllCloseTravelList(Date start,Date end, ArrayList<Travel> temp) {
        ArrayList<Travel> historyTravels = new  ArrayList<Travel>();
        for (Travel travel:temp) {
            if(travel.getArrivalDate().after(start) && travel.getTravelDate().before(end)) {
                    historyTravels.add(travel);
            }
        }
        return historyTravels;
    }
}