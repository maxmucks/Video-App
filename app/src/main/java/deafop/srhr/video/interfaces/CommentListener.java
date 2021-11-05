package deafop.srhr.video.interfaces;

import java.util.ArrayList;

import deafop.srhr.video.item.itemComment;



public interface CommentListener {
    void onStart();
    void onEnd(String success, ArrayList<itemComment> arrayListCat);
}
