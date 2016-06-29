package com.hots.desert.hotstc.library;

/**
 * Created by deser on 17.06.2016.
 */
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hots.desert.hotstc.HeroListActivity;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */


public class hotslogsheroes {

    //URL to get JSON Array
    private static String url = "https://api.hotslogs.com/Public/Data/Heroes";
    // Progress dialog
    private ProgressDialog pDialog;

    // temporary string to show the parsed response
    private static String jsonResponse;

    //JSON Node Names
    private static String stgroup = "Subgroup";
    private static String tgroup = "Group";
    private static  String name = "PrimaryName";
    private static  String imageurl = "ImageURL";
    private static String completepicurl= "Pic Url";
    public static int i = 0;
    private static RequestQueue queue;

    JSONArray user = null;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<heroes> ITEMS = new ArrayList<heroes>();
    public static final List<heroespics> HEROESPICS = new ArrayList<heroespics>();

    public static final Map<String, heroes> ITEM_MAP = new HashMap<String, heroes>();
    public static final Map<String, heroespics> HEROESPIC_MAP = new HashMap<String, heroespics>();

    public static final HeroListActivity hla = new HeroListActivity();
    /**
     *
     * A dummy item representing a piece of content.
     */

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void makeJsonArrayRequest() {

        //Cache cache = new DiskBasedCache(getCacheDir(), 2048 * 2048);
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    if (response.length() == hla.adapter.getItemCount()){

                            }
                            else{
                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject heroes = (JSONObject) response.get(i);

                                name = heroes.getString("PrimaryName");
                                imageurl = heroes.getString("ImageURL");
                                tgroup = heroes.getString("Group");
                                stgroup = heroes.getString("SubGroup");

                                jsonResponse += name;
                                jsonResponse += imageurl;
                                jsonResponse += tgroup;
                                jsonResponse += stgroup;
                                addItem(createheroes(i, name));
                                completepicurl = "http://d1i1jxrdh2kvwy.cloudfront.net/Images/Heroes/Portraits/"+ imageurl + ".png";
                                VolleyLog.d(completepicurl);
                                addItemPic(createheroepics(i, completepicurl));

                                VolleyLog.d("Loaded No. " + i);
                                int check = hla.adapter.getItemCount();
                                VolleyLog.d("Items = " + check);
                                hla.adapter.notifyItemInserted(check);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error:");
                addItem(createheroes(1, "yolo"));
            }
        });

        AppController.getInstance().addToRequestQueue(req);
        VolleyLog.d("Requestqueue Job added");
    }
    private void addItem(heroes item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    private void addItemPic(heroespics item){
        HEROESPICS.add(item);
    }

    private heroes createheroes(int position, String name) {
        VolleyLog.d(completepicurl + " **** " + HEROESPICS + " ");
        return new heroes(String.valueOf(position), name, makeDetails(position));
    }

    private heroespics createheroepics(int position, String completepicurl) {
        return new heroespics(position, completepicurl);
    }

        private String makeDetails ( int position){
        StringBuilder builder = new StringBuilder();
        builder.append(imageurl).append(" ").append(position);
       /* for (int i = 0; i < position; i++) {
            builder.append("MEEEEEHHRRRR DETAILZZZZ");
        }*/
        return builder.toString();
    }

    public static class heroespics {
        public final int picid;
        public final String pictureurl;

        public heroespics(int picid, String pictureurl){
            this.picid = picid;
            this.pictureurl = pictureurl;


            }
            @Override
            public String toString() {
                return pictureurl;
        }
    }

    public static class heroes {
        public final String id;
        public final String content;
        public final String details;

        public heroes(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
