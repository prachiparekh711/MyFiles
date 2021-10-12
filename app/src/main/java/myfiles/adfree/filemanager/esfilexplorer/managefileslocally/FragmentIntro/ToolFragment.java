package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Drive.DriveActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class ToolFragment extends BaseFragment{

    ImageView vaultTool,compressTool;
    View view;

    public ToolFragment(){
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
        view= inflater.inflate(R.layout.fragment_tool,container,false);
        init();
        return view;
    }

    public void init(){
        vaultTool=view.findViewById(R.id.vaultTool);
        compressTool=view.findViewById(R.id.compressTool);

        vaultTool.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(), VaultActivity.class);
                startActivity(in);
            }
        });

        compressTool.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(), DriveActivity.class);
                startActivity(in);
            }
        });
    }
}