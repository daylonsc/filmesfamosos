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

import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmeAdapter extends RecyclerView.Adapter<FilmeAdapter.FilmeAdapterViewHolder> {

    private List<Filme> filmes;
    private Context context;
    private Parcelable layoutManagerSavedState;
    private RecyclerView recyclerView;

    private final FilmeAdapterOnClickHandler mClickHandler;


    public interface FilmeAdapterOnClickHandler {
        void onClick(Filme filme);
    }

    public FilmeAdapter(FilmeAdapterOnClickHandler clickHandler, Parcelable layoutManagerSavedState, RecyclerView recyclerView) {
        mClickHandler = clickHandler;
        this.layoutManagerSavedState = layoutManagerSavedState;
        this.recyclerView = recyclerView;
    }

    public class FilmeAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView ivFromUrl;

        public FilmeAdapterViewHolder(View view) {
            super(view);
            ivFromUrl = (ImageView) view.findViewById(R.id.iv_from_url);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Filme filme = filmes.get(adapterPosition);
            mClickHandler.onClick(filme);
        }
    }

    @Override
    public FilmeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.filme_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FilmeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilmeAdapterViewHolder FilmeAdapterViewHolder, int position) {
        Filme filme = this.filmes.get(position);
        Picasso
                .with(context)
                .load(filme.getCaminhoImgPoster())
                .into(FilmeAdapterViewHolder.ivFromUrl);

    }

    @Override
    public int getItemCount() {
        if (null == this.filmes || this.filmes.isEmpty()) return 0;
        return this.filmes.size();
    }

    public void setFilmeData(List<Filme> filmes) {
        this.filmes = filmes;
        notifyDataSetChanged();
        restoreLayoutManagerPosition();
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }


}