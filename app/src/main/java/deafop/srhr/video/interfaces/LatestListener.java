package deafop.srhr.video.interfaces;

import java.util.ArrayList;

import deafop.srhr.video.item.itemVideo;




public interface LatestListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<itemVideo> arrayListVideo);
}
