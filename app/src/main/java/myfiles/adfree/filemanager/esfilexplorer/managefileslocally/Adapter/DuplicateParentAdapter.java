package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.ParentModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;

public class DuplicateParentAdapter extends RecyclerView.Adapter<DuplicateParentAdapter.MyClassView> {

    List<ParentModel> files;
    Activity activity;
    SelectionInterface selectionInterface;
    DuplicateChildAdapter mChildAdapter;
    DuplicateChildListAdapter mChildListAdapter;
    public static int mPosToDeselect=-1;
    public static int mPosToSelect=-1;
    public static boolean viewClose=false;

    public DuplicateParentAdapter(List<ParentModel> files,Activity activity) {
        this.files = files;
        this.activity = activity;
        selectionInterface=new SelectionInterface(){
            @Override
            public void onSelectItem(){
                notifyDataSetChanged();
            }

            @Override
            public void onDeselectDate(int pos){
                mPosToDeselect=pos;
                notifyDataSetChanged();
            }

            @Override
            public void onSelectDate(int position){
                mPosToSelect=position;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public DuplicateParentAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.parent_item_duplicate, parent, false);
        return new DuplicateParentAdapter.MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DuplicateParentAdapter.MyClassView holder,int position) {

        ParentModel parentItem     = files.get(position);

        holder.ParentItemTitle .setText(parentItem.getParentItemTitle());

        if (Util.VIEW_TYPE=="Grid") {
            final GridLayoutManager layoutManager = new GridLayoutManager(activity, Util.COLUMN_TYPE);

            mChildAdapter  = new DuplicateChildAdapter(parentItem.getChildItemList(),activity);
            holder.ChildRecyclerView.setLayoutManager(layoutManager);
            holder.ChildRecyclerView.setAdapter(mChildAdapter);
        } else {
            holder.ChildRecyclerView.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.VERTICAL,false));
            mChildListAdapter = new DuplicateChildListAdapter(parentItem.getChildItemList(),activity);
            holder.ChildRecyclerView.setAdapter(mChildListAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        private final TextView ParentItemTitle;
        private final RecyclerView ChildRecyclerView;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            ParentItemTitle= itemView .findViewById(R.id.parent_item_title);
            ChildRecyclerView = itemView .findViewById(   R.id.child_recyclerview);
        }
    }

}

