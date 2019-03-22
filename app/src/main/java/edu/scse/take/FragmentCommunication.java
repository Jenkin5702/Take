package edu.scse.take;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FragmentCommunication extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_communication,container,false);
        ListView lvCommunication=view.findViewById(R.id.lv_communication);
        TabLayout tbCommunication=view.findViewById(R.id.tabLayout2);
        FloatingActionButton fab=view.findViewById(R.id.float_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ActivityNewPicture.class);
                getContext().startActivity(intent);
            }
        });
        List<ItemBeanCommunication> list=new ArrayList<>();
        for(int i=0;i<8;i++){
            list.add(new ItemBeanCommunication(R.drawable.ss,
                    R.drawable.bg,
                    "12:27","\'radiance","This is content and following is the picturererere~~~~~"," "));
        }
        ListAdapterCommunication adapterCommunication=new ListAdapterCommunication(list,getContext());

        lvCommunication.setAdapter(adapterCommunication);
        return view;
    }
}
