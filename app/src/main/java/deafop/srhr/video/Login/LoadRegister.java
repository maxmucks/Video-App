package deafop.srhr.video.Login;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;


import okhttp3.RequestBody;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.SharedPref.Setting;

public class LoadRegister extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadRegister(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Setting.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Setting.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Setting.TAG_SUCCESS);
                message = c.getString(Setting.TAG_MSG);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, message);
        super.onPostExecute(s);
    }
}