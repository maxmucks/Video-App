package deafop.srhr.video.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import deafop.srhr.video.item.itemVideo;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "all.db";
    private SQLiteDatabase db;
    private final Context context;

    private static String TAG_ID = "id";

    private static String TAG_cat_id = "cat_id";
    private static String TAG_video_title = "video_title";
    private static String TAG_video_id = "video_id";

    private static String TAG_video_type = "video_type";
    private static String TAG_video_thumbnail = "video_thumbnail";

    private static String TAG_video_url = "video_url";
    private static String TAG_total_views = "total_views";
    private static String TAG_video_description = "video_description";
    private static String TAG_video_date = "video_date";
    private static String TAG_cid = "cid";
    private static String TAG_category_name = "category_name";
    private static String TAG_category_image = "category_image";
    private static String TAG_category_image_thumb = "category_image_thumb";


    public static final String TABLE_FAV_VIDEO = "video";


    private String[] columns_video = new String[]{TAG_ID, TAG_cat_id, TAG_video_title, TAG_video_id, TAG_video_type, TAG_video_thumbnail,
            TAG_video_url, TAG_total_views, TAG_video_description, TAG_video_date, TAG_cid ,TAG_category_name, TAG_category_image, TAG_category_image_thumb};

    // Creating table query
    private static final String CREATE_TABLE_FAV = "create table " + TABLE_FAV_VIDEO + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_cat_id + " TEXT," +
            TAG_video_title + " TEXT," +
            TAG_video_id + " TEXT," +
            TAG_video_type + " TEXT," +
            TAG_video_thumbnail + " TEXT," +
            TAG_video_url + " TEXT," +
            TAG_total_views + " TEXT," +
            TAG_video_description + " TEXT," +
            TAG_video_date + " TEXT," +
            TAG_cid + " TEXT," +
            TAG_category_name + " TEXT," +
            TAG_category_image + " TEXT," +
            TAG_category_image_thumb + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
        db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_FAV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<itemVideo> getVideo(String table) {
        ArrayList<itemVideo> arrayList = new ArrayList<>();

        Cursor cursor = db.query(table, columns_video, TAG_ID, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                String id = cursor.getString(cursor.getColumnIndex(TAG_ID));
                String cat_id = cursor.getString(cursor.getColumnIndex(TAG_cat_id));
                String title = cursor.getString(cursor.getColumnIndex(TAG_video_title));
                String video_id = cursor.getString(cursor.getColumnIndex(TAG_video_id));
                String video_type = cursor.getString(cursor.getColumnIndex(TAG_video_type));
                String video_thumbnail = cursor.getString(cursor.getColumnIndex(TAG_video_thumbnail));
                String video_url = cursor.getString(cursor.getColumnIndex(TAG_video_url));
                String total_views = cursor.getString(cursor.getColumnIndex(TAG_total_views));
                String video_description = cursor.getString(cursor.getColumnIndex(TAG_video_description));
                String video_date = cursor.getString(cursor.getColumnIndex(TAG_video_date));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_cid));
                String category_name = cursor.getString(cursor.getColumnIndex(TAG_category_name));
                String category_image = cursor.getString(cursor.getColumnIndex(TAG_category_image));
                String category_image_thumb = cursor.getString(cursor.getColumnIndex(TAG_category_image_thumb));

                itemVideo objItem = new itemVideo(id, cat_id, title, video_id, video_type, video_thumbnail, video_url, total_views, video_description, video_date, cid, category_name, category_image, category_image_thumb);
                arrayList.add(objItem);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }



    public Boolean isFav(String id) {

        String where = TAG_ID + "=?";
        String[] args = {id};

        Cursor cursor = db.query(TABLE_FAV_VIDEO, columns_video, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void addtoFavorite(itemVideo itemVideo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_ID, itemVideo.getId());
        contentValues.put(TAG_cat_id, itemVideo.getCat_id());
        contentValues.put(TAG_video_title, itemVideo.getVideo_title());
        contentValues.put(TAG_video_id, itemVideo.getVideo_id());
        contentValues.put(TAG_video_type, itemVideo.getVideo_type());
        contentValues.put(TAG_video_thumbnail, itemVideo.getVideo_thumbnail());
        contentValues.put(TAG_video_url, itemVideo.getVideo_url());
        contentValues.put(TAG_total_views, itemVideo.getTotal_views());
        contentValues.put(TAG_cid, itemVideo.getCid());
        contentValues.put(TAG_category_name, itemVideo.getCategory_name());
        contentValues.put(TAG_category_image, itemVideo.getCategory_image());
        contentValues.put(TAG_category_image_thumb, itemVideo.getCategory_image_thumb());

        db.insert(TABLE_FAV_VIDEO, null, contentValues);
    }

    public void removeFav(String id) {
        db.delete(TABLE_FAV_VIDEO, TAG_ID + "=" + id, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("aaa", "upgrade");
        Log.e("aaa -oldVersion", "" + oldVersion);
        Log.e("aaa -newVersion", "" + newVersion);
    }
}