package com.hots.desert.hotstc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.app.ActionBar;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hots.desert.hotstc.dummy.DummyContent;
import com.hots.desert.hotstc.library.AppController;
import com.hots.desert.hotstc.library.hotslogsheroes;

import java.util.List;

import static android.app.PendingIntent.getActivity;


/**
 * An activity representing a list of Heroes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HeroDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HeroListActivity extends AppCompatActivity{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public View recyclerView;
    public RecyclerView.Adapter adapter;

    {
        adapter = new SimpleItemRecyclerViewAdapter(hotslogsheroes.ITEMS, hotslogsheroes.HEROESPICS);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_hero_list);
        // Show the Up button in the action bar.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.hero_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.hero_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //RecyclerView.Adapter adapter = new SimpleItemRecyclerViewAdapter(hotslogsheroes.ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        VolleyLog.d("setAdapter on Recycle View");


    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<hotslogsheroes.heroes> mValues;
        private final List<hotslogsheroes.heroespics> mValuesPic;

        public SimpleItemRecyclerViewAdapter(List<hotslogsheroes.heroes> items, List<hotslogsheroes.heroespics> heroespics) {
            mValues = items;
            mValuesPic = heroespics;
        }
        public Cache getCache(){
            Cache cache = new DiskBasedCache(getCacheDir(), 4096 * 4096);
            return cache;
        }
        ImageLoader imageLoader = new ImageLoader(AppController.getInstance().getRequestQueue(), new ImageLoader.ImageCache() {

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                //put(url, bitmap);
            }
        });


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hero_list_content, parent, false);
            VolleyLog.d("***OnCreateViewHolder***");
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            VolleyLog.d(/*"***mPictures= " + mValuesPic + */" *** "+ " ***imageLoader= "+ imageLoader + "***pictureurl= " + mValuesPic.get(position).pictureurl);

            holder.mNetworkView.setImageUrl(mValuesPic.get(position).pictureurl, imageLoader);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(HeroDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        HeroDetailFragment fragment = new HeroDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.hero_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, HeroDetailActivity.class);
                        intent.putExtra(HeroDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                        VolleyLog.d("***End of BindView (start Activity)***");

                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            VolleyLog.d("GET ITEM COUNT ="+ mValues.size());
            return mValues.size();

        }
        public int getItemPicCount() {
            VolleyLog.d("GET ITEM COUNT ="+ mValuesPic.size());
            return mValuesPic.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public final NetworkImageView mNetworkView;
            public hotslogsheroes.heroes mItem;
            public hotslogsheroes.heroespics mItempics;


            public ViewHolder(View view) {
                super(view);
                mNetworkView = (NetworkImageView) view.findViewById(R.id.networkimageview);
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
