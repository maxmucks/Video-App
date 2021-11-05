package deafop.srhr.video.Receiver;



public interface NemosoftsListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message);
}