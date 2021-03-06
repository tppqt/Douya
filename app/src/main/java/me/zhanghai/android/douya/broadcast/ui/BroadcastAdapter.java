/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BroadcastAdapter extends SimpleAdapter<Broadcast, BroadcastAdapter.ViewHolder> {

    private Listener mListener;

    public BroadcastAdapter(List<Broadcast> broadcastList, Listener listener) {
        super(broadcastList);

        mListener = listener;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.broadcast_item, parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Broadcast originalBroadcast = getItem(position);
        holder.rebroadcastedByText.setText(originalBroadcast.getRebroadcastedBy(
                RecyclerViewUtils.getContext(holder)));
        final Broadcast broadcast = originalBroadcast.rebroadcastedBroadcast != null ?
                originalBroadcast.rebroadcastedBroadcast : originalBroadcast;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOpenBroadcast(broadcast, getSharedView(holder));
            }
        });
        holder.broadcastLayout.bindBroadcast(broadcast);
        holder.broadcastLayout.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLikeBroadcast(broadcast, !broadcast.isLiked);
            }
        });
        holder.broadcastLayout.mRebroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRebroadcastBroadcast(broadcast, !broadcast.isRebroadcasted());
            }
        });
        holder.broadcastLayout.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Not setting button disabled because we are using enabled state for indeterminate
                // state due to network, and we want the click to always open the broadcast for our
                // user.
                mListener.onCommentBroadcast(broadcast, getSharedView(holder));
            }
        });
    }

    private static View getSharedView(ViewHolder holder) {
        Context context = holder.itemView.getContext();
        // HACK: Transition is so hard to work with, but this gives a better effect.
        View view = ViewUtils.hasSw600dp(context) ? holder.cardView : holder.broadcastLayout;
        ViewCompat.setTransitionName(view, context.getString(R.string.transition_name_broadcast));
        return view;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.broadcastLayout.releaseBroadcast();
    }

    public interface Listener {
        void onLikeBroadcast(Broadcast broadcast, boolean like);
        void onRebroadcastBroadcast(Broadcast broadcast, boolean rebroadcast);
        void onCommentBroadcast(Broadcast broadcast, View sharedView);
        void onOpenBroadcast(Broadcast broadcast, View sharedView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.rebroadcasted_by)
        public TextView rebroadcastedByText;
        @Bind(R.id.card)
        public CardView cardView;
        @Bind(R.id.broadcast)
        public BroadcastLayout broadcastLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
