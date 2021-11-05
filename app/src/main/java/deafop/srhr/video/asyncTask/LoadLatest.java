package deafop.srhr.video.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


import okhttp3.RequestBody;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.LatestListener;



public class LoadLatest extends AsyncTask<String, String, String> {

    private LatestListener latestListener;
    private ArrayList<itemVideo> arrayList;
    private RequestBody requestBody;
    private String verifyStatus = "0", message = "";

    public LoadLatest(LatestListener latestListener, RequestBody requestBody) {
        this.latestListener = latestListener;
        arrayList = new ArrayList<>();
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        latestListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected  String doInBackground(String... strings)  {
        String json = JSONParser.okhttpPost(Setting.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Setting.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Setting.TAG_SUCCESS)) {

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


                    itemVideo itemWallpaper = new itemVideo(id, cat_id, video_title, video_id, video_type, video_thumbnail, video_url, total_views,video_description, video_date, cid, category_name, category_image, category_image_thumb);

                    arrayList.add(itemWallpaper);
                } else {
                    verifyStatus = objJson.getString(Setting.TAG_SUCCESS);
                    message = objJson.getString(Setting.TAG_MSG);
                }

            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        latestListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }

}

