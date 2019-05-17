/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.android.com.filmesfamosos;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private List<String> trailers;
    private Context context;

    private final TrailerAdapterOnClickHandler mClickHandler;


    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailer);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView ivPlayYoutube;
        public TextView tvPlayYoutube;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            ivPlayYoutube = (ImageView) view.findViewById(R.id.iv_play_youtube);
            tvPlayYoutube = (TextView) view.findViewById(R.id.tv_play_youtube);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String trailer = trailers.get(adapterPosition);
            mClickHandler.onClick(trailer);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder TrailerAdapterViewHolder, int position) {
        String trailer = this.trailers.get(position);
        int ordem = position + 1;
        TrailerAdapterViewHolder.tvPlayYoutube.setText("Trailer "+ ordem);
    }

    @Override
    public int getItemCount() {
        if (null == this.trailers || this.trailers.isEmpty()) return 0;
        return this.trailers.size();
    }

    public void setTrailerData(List<String> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}