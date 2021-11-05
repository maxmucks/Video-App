package deafop.srhr.video.item;


public class itemComment {

    private String comment;
    private String user_name;
    private String comment_text;


    public itemComment(String comment, String user_name, String comment_text) {
        this.comment = comment;
        this.user_name = user_name;
        this.comment_text = comment_text;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getComment_text() {
        return comment_text;
    }


}