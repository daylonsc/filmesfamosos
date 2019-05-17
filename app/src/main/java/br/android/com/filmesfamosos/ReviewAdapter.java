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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<String> reviews;
    private Context context;

    public interface ReviewAdapterOnClickHandler {
        void onClick(String trailer);
    }

    public ReviewAdapter() {
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView tvReview;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            tvReview = (TextView) view.findViewById(R.id.tv_review);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder ReviewAdapterViewHolder, int position) {
        String review = this.reviews.get(position);
        int ordem = position + 1;
        ReviewAdapterViewHolder.tvReview.setText(review);
    }

    @Override
    public int getItemCount() {
        if (null == this.reviews || this.reviews.isEmpty()) return 0;
        return this.reviews.size();
    }

    public void setReviewData(List<String> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}