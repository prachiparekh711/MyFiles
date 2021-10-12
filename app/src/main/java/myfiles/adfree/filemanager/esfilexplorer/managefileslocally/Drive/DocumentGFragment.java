package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Drive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentGFragment extends Fragment{

    @BindView(R.id.rv_img_download)
    RecyclerView rv_img_download;
    @BindView(R.id.no_found)
    TextView no_found;

    @BindView(R.id.rl_List)
    RelativeLayout rl_List;
    @BindView(R.id.rv_doc_download_list)
    RecyclerView rv_doc_download_list;

    AllGdriveAdapter imgDownloadAdapter;
    AllGridGdriveAdapter imgDownloadListAdapter;

    ArrayList<BaseModel> imgDownloadList = new ArrayList<>();
    ArrayList<BaseModel> imgMainDownloadList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drive_all, container, false);
        ButterKnife.bind(this, view);

        getData();

        rv_img_download.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rv_img_download.setNestedScrollingEnabled(false);

        rv_doc_download_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_doc_download_list.setNestedScrollingEnabled(false);

        imgDownloadListAdapter = new AllGridGdriveAdapter(imgMainDownloadList, getActivity(), "document");
        rv_img_download.setAdapter(imgDownloadListAdapter);

        imgDownloadAdapter = new AllGdriveAdapter(imgMainDownloadList, getActivity(), "document");
        rv_doc_download_list.setAdapter(imgDownloadAdapter);

        onScrolledToBottom();

        rv_img_download.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,int dx,int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                    onScrolledToBottom();
            }
        });

        rv_doc_download_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,int dx,int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                    onScrolledToBottom();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getData() {
        imgDownloadList.clear();


        for (int i = 0; i < DriveActivity.gDriveArrayList.size(); i++) {
            String path = DriveActivity.gDriveArrayList.get(i).getName();
            //   Log.e("LLLLL_Log: ", path);
            if (path.contains(".docx") ||
                    path.contains(".pdf") ||
                    path.contains(".txt") ||
                    path.contains(".xml") ||
                    path.contains(".ppt") ||
                    path.contains(".pptx") ||
                    path.contains(".html")) {
                imgDownloadList.add(DriveActivity.gDriveArrayList.get(i));
            }
        }

    }

    private void onScrolledToBottom() {

        if (imgMainDownloadList.size() < imgDownloadList.size()) {
            int x, y;
            if ((imgDownloadList.size() - imgMainDownloadList.size()) >= 40) {
                x = imgMainDownloadList.size();
                y = x + 40;
            } else {
                x = imgMainDownloadList.size();
                y = x + imgDownloadList.size() - imgMainDownloadList.size();
            }
            for (int i = x; i < y; i++) {
                imgMainDownloadList.add(imgDownloadList.get(i));
            }
            getActivity().runOnUiThread(() -> {
                imgDownloadAdapter.notifyDataSetChanged();
                imgDownloadListAdapter.notifyDataSetChanged();
            });
        }
    }
}
