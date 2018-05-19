package com.example.administrator.cameraalbum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 适配器
 */
public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.MyViewHolder> {

    private Context mContext;
    private List<MyImage> myImageList;
    private MyClickItem listener;

    public interface MyClickItem {
        void onIClick(View view, int position);//增加
        void onDClick(int position);//删除
    }

    public void setListener(MyClickItem listener) {
        this.listener = listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDis;
        ImageView imageViewDel;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageViewDis = itemView.findViewById(R.id.disp_image);
            imageViewDel = itemView.findViewById(R.id.x_dele);
        }
    }

    public MyImageAdapter(List<MyImage> myImageList) {
        this.myImageList = myImageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycle_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (listener != null) {

            holder.imageViewDis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加号的点击事件
                    listener.onIClick(v, holder.getAdapterPosition());
                }
            });

        }

        MyImage mImage = myImageList.get(position);
        if (mImage.getType() == MyImage.ADDIM) {
            //添加图片的响应事件
            Glide.with(mContext).load(mImage.getAddId()).into(holder.imageViewDis);
            //把删除的图标隐藏
            holder.imageViewDel.setVisibility(View.GONE);



        } else if (mImage.getType() == MyImage.MYIMAGE) {

            Glide.with(mContext).load(mImage.getImagePath()).into(holder.imageViewDis);
            holder.imageViewDel.setVisibility(View.VISIBLE);
            //删除的点击事件
            holder.imageViewDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(mContext, "删除:" + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();


                    myImageList.remove(holder.getAdapterPosition());
                 //   notifyDataSetChanged();

                  //删除要同步到album
             listener.onDClick(holder.getAdapterPosition());
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return myImageList.size();
    }


}
