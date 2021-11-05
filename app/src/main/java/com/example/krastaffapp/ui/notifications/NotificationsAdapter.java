package com.example.krastaffapp.ui.notifications;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krastaffapp.R;

import java.util.List;

public class NotificationsAdapter  extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    List<NotificationsList> nLists;

    NotificationsDB notificationsDB;

    RecyclerView recyclerView;


    public NotificationsAdapter(List<NotificationsList> nLists, NotificationsDB notificationsDB, RecyclerView recyclerView) {

        this.nLists = nLists;
        this.notificationsDB = notificationsDB;
        this.recyclerView = recyclerView;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_cardview,viewGroup,false);



        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {


        NotificationsList md = nLists.get(i);

        String nId = md.getId();

        viewHolder.txttitle.setText(md.getNtitle());
        viewHolder.txtcontent.setText(md.getNmessage());
        viewHolder.txttime.setText(md.getNtime());
        String rr = md.getNread();


        if (rr.equals("unread")) {

            viewHolder.txttitle.setTypeface(viewHolder.txttitle.getTypeface(), Typeface.BOLD);
            viewHolder.txtcontent.setTypeface(viewHolder.txtcontent.getTypeface(), Typeface.BOLD);
            viewHolder.txttime.setTypeface(viewHolder.txttime.getTypeface(), Typeface.BOLD);


        }else if (rr.equals("opened")){

            viewHolder.txttitle.setTypeface(Typeface.create(viewHolder.txttitle.getTypeface(), Typeface.NORMAL));
            viewHolder.txtcontent.setTypeface(Typeface.create(viewHolder.txtcontent.getTypeface(), Typeface.NORMAL));
            viewHolder.txttime.setTypeface(Typeface.create(viewHolder.txttime.getTypeface(), Typeface.NORMAL));

        }


        viewHolder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsDB.notificationsDAO().delete(md);
                nLists.remove(i);
                notifyDataSetChanged();
                recyclerView.removeView(view);

            }
        });

        viewHolder.btnmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(rr.equals("unread")) {

                    notificationsDB.notificationsDAO().updateNotif("opened", nId);


                    viewHolder.txttitle.setTypeface(Typeface.create(viewHolder.txttitle.getTypeface(), Typeface.NORMAL));
                    viewHolder.txtcontent.setTypeface(Typeface.create(viewHolder.txtcontent.getTypeface(), Typeface.NORMAL));
                    viewHolder.txttime.setTypeface(Typeface.create(viewHolder.txttime.getTypeface(), Typeface.NORMAL));

                    recyclerView.removeView(view);


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return nLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txttitle, txtcontent, txttime;
        private final Button btndelete, btnmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txttitle= itemView.findViewById(R.id.tvTitle);
            txtcontent= itemView.findViewById(R.id.tvContent);
            txttime= itemView.findViewById(R.id.tvTime);
            btnmark = itemView.findViewById(R.id.btnMark);
            btndelete = itemView.findViewById(R.id.btnDelete);



        }
    }






}