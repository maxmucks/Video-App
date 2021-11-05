package deafop.srhr.video.asyncTask;

import android.os.AsyncTask;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.item.ItemHomeBanner;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.HomeListener;

public class LoadHome extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private HomeListener homeListener;
    private ArrayList<ItemHomeBanner> arrayListBanner = new ArrayList<>();
    private ArrayList<itemVideo> arrayList_home1 = new ArrayList<>();
    private ArrayList<itemVideo> arrayList_home2 = new ArrayList<>();
    private ArrayList<itemVideo> arrayList_home3 = new ArrayList<>();
    private ArrayList<itemCategory> arrayList_cat = new ArrayList<>();

    public LoadHome(HomeListener homeListener, RequestBody requestBody) {
        this.homeListener = homeListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String json = JSONParser.okhttpPost(Setting.SERVER_URL, requestBody);

            JSONObject mainJson = new JSONObject(json);
            JSONObject jsonObject = mainJson.getJSONObject(Setting.TAG_ROOT);


            JSONArray jsonArrayBanner = jsonObject.getJSONArray("home_banner");

            for (int i = 0; i < jsonArrayBanner.length(); i++) {
                JSONObject objJsonBanner = jsonArrayBanner.getJSONObject(i);

                String banner_id = objJsonBanner.getString("bid");
                String banner_title = objJsonBanner.getString("banner_title");
                String banner_desc = objJsonBanner.getString("banner_sort_info");
                String banner_image = objJsonBanner.getString("banner_image");
                String banner_total = objJsonBanner.getString("total_songs");

                JSONArray jABannerSongs = objJsonBanner.getJSONArray("songs_list");
                ArrayList<itemVideo> arrayListBannerSongs = new ArrayList<>();
                for (int j = 0; j < jABannerSongs.length(); j++) {
                    JSONObject objJson = jABannerSongs.getJSONObject(j);

                    String id = objJson.getString("id");
                    String cat_id = objJson.getString("cat_id");
                    String video_title = objJson.getString("video_title");
                    String video_id = objJson.getString("video_id");
                    String video_type = objJson.getString("video_type");
                    String video_thumbnail = objJson.getString("video_thumbnail");
                    String video_url = objJson.getString("video_url");
                    String total_views = objJson.getString("total_views");
                    String video_description = objJson.getString("video_description");
                    String video_date = objJson.getString("video_date");
                    String cid = objJson.getString("cid");
                    String category_name = objJson.getString("category_name");
                    String category_image = objJson.getString("category_image");
                    String category_image_thumb = objJson.getString("category_image_thumb");

                    itemVideo item = new itemVideo(id, cat_id, video_title, video_id, video_type, video_thumbnail, video_url, total_views, video_description, video_date, cid, category_name, category_image, category_image_thumb);
                    arrayListBannerSongs.add(item);
                }

                arrayListBanner.add(new ItemHomeBanner(banner_id, banner_title, banner_image, banner_desc, banner_total, arrayListBannerSongs));
            }



            JSONArray jsonArrayHome1 = jsonObject.getJSONArray("latest_video");
            for (int i = 0; i < jsonArrayHome1.length(); i++) {
                JSONObject objJson = jsonArrayHome1.getJSONObject(i);
                itemVideo item = new itemVideo(
                        objJson.getString("id"),
                        objJson.getString("cat_id"),
                        objJson.getString("video_title"),
                        objJson.getString("video_id"),
                        objJson.getString("video_type"),
                        objJson.getString("video_thumbnail"),
                        objJson.getString("video_url"),
                        objJson.getString("total_views"),
                        objJson.getString("video_description"),
                        objJson.getString("video_date"),
                        objJson.getString("cid"),
                        objJson.getString("category_name"),
                        objJson.getString("category_image"),
                        objJson.getString("category_image_thumb")
                );
                arrayList_home1.add(item);
            }

            JSONArray jsonArrayHome2 = jsonObject.getJSONArray("all_video");
            for (int i = 0; i < jsonArrayHome2.length(); i++) {
                JSONObject objJson = jsonArrayHome2.getJSONObject(i);
                itemVideo item = new itemVideo(
                        objJson.getString("id"),
                        objJson.getString("cat_id"),
                        objJson.getString("video_title"),
                        objJson.getString("video_id"),
                        objJson.getString("video_type"),
                        objJson.getString("video_thumbnail"),
                        objJson.getString("video_url"),
                        objJson.getString("total_views"),
                        objJson.getString("video_description"),
                        objJson.getString("video_date"),
                        objJson.getString("cid"),
                        objJson.getString("category_name"),
                        objJson.getString("category_image"),
                        objJson.getString("category_image_thumb")
                );
                arrayList_home2.add(item);
            }

            JSONArray jsonArrayHome3 = jsonObject.getJSONArray("most_video");
            for (int i = 0; i < jsonArrayHome3.length(); i++) {
                JSONObject objJson = jsonArrayHome3.getJSONObject(i);
                itemVideo item = new itemVideo(
                        objJson.getString("id"),
                        objJson.getString("cat_id"),
                        objJson.getString("video_title"),
                        objJson.getString("video_id"),
                        objJson.getString("video_type"),
                        objJson.getString("video_thumbnail"),
                        objJson.getString("video_url"),
                        objJson.getString("total_views"),
                        objJson.getString("video_description"),
                        objJson.getString("video_date"),
                        objJson.getString("cid"),
                        objJson.getString("category_name"),
                        objJson.getString("category_image"),
                        objJson.getString("category_image_thumb")
                );
                arrayList_home3.add(item);
            }

            JSONArray jsonArraycat = jsonObject.getJSONArray("Home_cat");
            for (int i = 0; i < jsonArraycat.length(); i++) {
                JSONObject objJson = jsonArraycat.getJSONObject(i);
                itemCategory item = new itemCategory(
                        objJson.getString("cid"),
                        objJson.getString("category_name"),
                        objJson.getString("category_image"),
                        objJson.getString("category_image_thumb")
                );
                arrayList_cat.add(item);
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        homeListener.onEnd(s, arrayListBanner, arrayList_home1, arrayList_home2, arrayList_home3, arrayList_cat);
        super.onPostExecute(s);
    }
}