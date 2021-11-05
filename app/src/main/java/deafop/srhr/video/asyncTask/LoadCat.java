package deafop.srhr.video.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


import okhttp3.RequestBody;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.CatListener;


public class LoadCat extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private CatListener catListener;
    private ArrayList<itemCategory> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadCat(CatListener catListener, RequestBody requestBody) {
        this.catListener = catListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        catListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Setting.SERVER_URL, requestBody);

            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Setting.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Setting.TAG_SUCCESS)) {
                    String cid = obj.getString(Setting.TAG_CID);
                    String category_name = obj.getString(Setting.TAG_CAT_NAME);
                    String category_image = obj.getString(Setting.TAG_CAT_IMAGE);
                    String category_image_thumb = obj.getString(Setting.TAG_CAT_IMAGE);

                    itemCategory objItem = new itemCategory(cid, category_name, category_image,category_image_thumb);
                    arrayList.add(objItem);
                } else {
                    verifyStatus = obj.getString(Setting.TAG_SUCCESS);
                    message = obj.getString(Setting.TAG_MSG);
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
        catListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}