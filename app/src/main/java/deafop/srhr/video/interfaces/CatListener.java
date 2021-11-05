package deafop.srhr.video.interfaces;

import java.util.ArrayList;


import deafop.srhr.video.item.itemCategory;


public interface CatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<itemCategory> arrayList);
}
