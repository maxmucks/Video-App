package deafop.srhr.video.Login;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message);
}