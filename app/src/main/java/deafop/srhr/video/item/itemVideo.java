package deafop.srhr.video.item;


public class itemVideo {

    private String id;
    private String cat_id;
    private String video_title;
    private String video_id;
    private String video_type;
    private String video_thumbnail;
    private String video_url;
    private String total_views;
    private String video_description;
    private String video_date;
    private String cid;
    private String category_name;
    private String category_image;
    private String category_image_thumb;


    public itemVideo(String id, String cat_id, String video_title, String video_id, String video_type, String video_thumbnail, String video_url, String total_views, String video_description, String video_date, String cid, String category_name, String category_image, String category_image_thumb) {
        this.id = id;
        this.cat_id = cat_id;
        this.video_title = video_title;
        this.video_id = video_id;
        this.video_type = video_type;
        this.video_thumbnail = video_thumbnail;
        this.video_url = video_url;
        this.total_views = total_views;
        this.video_description = video_description;
        this.video_date = video_date;
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.category_image_thumb = category_image_thumb;
    }



    public String getId() {
        return id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getVideo_type() {
        return video_type;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getTotal_views() {
        return total_views;
    }

    public String getVideo_description() {
        return video_description;
    }

    public String getVideo_date() {
        return video_date;
    }

    public String getCid() {
        return cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

}
