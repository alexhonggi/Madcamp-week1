package com.example.madcamp_week1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;

public class ImageFragment extends Fragment {


    private ImageView imageView;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image, container, false);
        // ViewGroup rootView2 = (ViewGroup) inflater.inflate(R.layout.image_layout, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        ArrayList imageUrlList = prepareData();
        DataAdapter dataAdapter = new DataAdapter(getActivity().getApplicationContext(), imageUrlList);
        recyclerView.setAdapter(dataAdapter);

        dataAdapter.setOnItemListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.
            }
        });

        return rootView;
    }

    private ArrayList prepareData() {

// here you should give your image URLs and that can be a link from the Internet
        String imageUrls[] = {
                "https://gregstoll.com/~gregstoll/baseballteamnames/images/hou_logo.jpg",
                "https://gregstoll.com/~gregstoll/baseballteamnames/images/tex_logo.jpg",
                "https://gregstoll.com/~gregstoll/baseballteamnames/images/sea_logo.jpg",
                "https://gregstoll.com/~gregstoll/baseballteamnames/images/oak_logo.jpg",
                "https://cdn.notefolio.net/img/2c/78/2c78905cc27b985c69c4ffd4a92f4786ec3c38cdf8cf579d071bc9e7b1425939_v1.jpg",
                "https://cdn.notefolio.net/img/e3/cc/e3ccc2e4e0b559a10f39c6bdee14c126520c8f96e51c6f623101ff6b0fae119b_v1.jpg",
                "https://cdn.notefolio.net/img/9f/81/9f81a5c93b89a212037632b284ff07a059cc400753fc7cffb738b9b4ff2cc682_v1.jpg",
                "https://cdn.notefolio.net/img/c5/be/c5be173f0d57edecc906462b85f1a20d8b835665f7b057c3877dd2b9b9529df3_v1.jpg",
                "https://cdn.notefolio.net/img/85/08/8508f77ad1fcfb7b943c535130b7477891cc6c41c9529aa744ea210171110ab6_v1.jpg",
                "https://i.pinimg.com/736x/57/7c/a6/577ca680478124cbb52ccc0bca00d1e9--water-illustration-illustration-served.jpg",
                "https://image-cdn.hypb.st/https%3A%2F%2Fhypebeast.com%2Fimage%2F2018%2F05%2Fwhite-rock-spring-summer-2018-editorial-2.jpg?q=90&w=1400&cbr=1&fit=max",
                "https://img.zcool.cn/community/01dbaa5e564f5ea80120a8958d12fc.jpg@1280w_1l_2o_100sh.jpg",
                "https://img.zcool.cn/community/0113c05e54e034a80121651874676f.jpg@1280w_1l_2o_100sh.jpg",
                "https://pbs.twimg.com/media/DE1iWzSVwAEzx5v.jpg:orig",
                "https://lh5.googleusercontent.com/-dpXKH-ohsIg/Use0hPlEEFI/AAAAAAAAitQ/1Vc_484KN-c/w500/arron-18.jpg",
                "https://i.etsystatic.com/6327391/r/il/b9d743/3226114107/il_794xN.3226114107_swpd.jpg",
                "https://i.etsystatic.com/6327391/r/il/e60552/2217533350/il_794xN.2217533350_2w5n.jpg",
                "https://i.etsystatic.com/6327391/r/il/b12b92/1896995750/il_794xN.1896995750_a0gn.jpg",
                "https://i.etsystatic.com/6327391/r/il/b3e51e/2904781210/il_794xN.2904781210_lzs2.jpg",
                "https://secure.img1-fg.wfcdn.com/im/70665064/resize-h800-w800%5Ecompr-r85/1687/16876933/%27Water+Lilies+III%27+Painting+Print+on+Canvas.jpg",
                "https://i.etsystatic.com/23057318/r/il/d6d6fe/2635201101/il_794xN.2635201101_trpf.jpg",
                "https://i.pinimg.com/originals/52/29/99/522999d230782db971a681aa762e8e2d.gif",
                "https://64.media.tumblr.com/e4676500ccfdc5ef53733ba6410903ab/tumblr_ozowsebQWC1u6glkso1_540.gifv",
                "https://images.fastcompany.net/image/upload/w_596,c_limit,q_auto:best,f_webm/fc/3049717-inline-i-1-horizon-copy.gif",
                "https://s3.amazonaws.com/www-inside-design/uploads/2019/01/kinetic-typography-5.gif",
                "https://s3.amazonaws.com/www-inside-design/uploads/2019/01/kinetic-typography-1.gif",
                "https://64.media.tumblr.com/8ab37589e31033a3cb4e97ecf04c9d65/tumblr_pbhbrtuQn61u6glkso1_540.gifv"
        };

        ArrayList imageUrlList = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            ImageUrl imageUrl = new ImageUrl();
            imageUrl.setImageUrl(imageUrls[i]);
            imageUrlList.add(imageUrl);
        }
        Log.d("ImageFragment", "List count: " + imageUrlList.size());
        return imageUrlList;
    }
}