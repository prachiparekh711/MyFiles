package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.AsyncActivity;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class CleanFragment extends BaseFragment{

    View view;

    CardView t1;

    public CleanFragment(){
        // Required empty public constructor
    }

    @Override
    void permissionGranted(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_clean,container,false);
        init();

        return view;
    }



    @Override
    public void onResume(){
        super.onResume();
    }



    public void init(){
        t1=view.findViewById(R.id.t1);

        t1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                Intent intent = new Intent(getActivity(), AsyncActivity.class);
                getActivity().startActivity(intent);
            }
        });


    }


}