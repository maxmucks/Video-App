package deafop.srhr.video.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.item.itemComment;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.CommentListener;



public class LoadComment extends AsyncTask<String, String, String> {

    private CommentListener commentListener;
    private ArrayList<itemComment> arrayList;
    private RequestBody requestBody;

    public LoadComment(CommentListener commentListener, RequestBody requestBody) {
        this.commentListener = commentListener;
        arrayList = new ArrayList<>();
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        commentListener.onStart();
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

                itemComment item = new itemComment(
                        objJson.getString("news_id"),
                        objJson.getString("user_name"),
                        objJson.getString("comment_text")
                );
                arrayList.add(item);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }


    }

    @Override
    protected void onPostExecute(String s) {
        commentListener.onEnd(String.valueOf(s),arrayList);
        super.onPostExecute(s);
    }

}

