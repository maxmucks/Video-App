package deafop.srhr.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;


public class AdapterVideo extends RecyclerView.Adapter {

    private ArrayList<itemVideo> arrayList;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;
    private Methods methods;


    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private NativeAdsManager mNativeAdsManager;
    private AdLoader adLoader = null;
    private List<UnifiedNativeAd> mNativeAdsAdmob = new ArrayList<>();
    private ArrayList<NativeAd> mNativeAdsFB = new ArrayList<>();

    private class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView name, category_name, textView_views, tv_date;
        public ImageView imageView, imageView_option;

        RelativeLayout cl;

        private MyViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.name);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            textView_views = (TextView) itemView.findViewById(R.id.textView_views);
            category_name = (TextView) itemView.findViewById(R.id.category_name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView_option = (ImageView) itemView.findViewById(R.id.imageView_option);

            cl = itemView.findViewById(R.id.cl);

        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    public AdapterVideo(Context context, ArrayList<itemVideo> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new Methods(context);
        this.recyclerViewClickListener = recyclerViewClickListener;

        loadNativeAds();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_video, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final itemVideo item = arrayList.get(position);

            ((MyViewHolder) holder).name.setText(item.getVideo_title());
            ((MyViewHolder) holder).category_name.setText(item.getCategory_name());
            ((MyViewHolder) holder).tv_date.setText(" "+item.getVideo_date());
            ((MyViewHolder) holder).textView_views.setText(format(Double.parseDouble((String)item.getTotal_views())));

            switch (item.getVideo_type()) {
                case "fb":
                    Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.fb).into(((MyViewHolder) holder).imageView);
                    break;
                case "local":
                    Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.local).into(((MyViewHolder) holder).imageView);
                    break;
                case "server_url":
                    Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.url_im).into(((MyViewHolder) holder).imageView);
                    break;
                case "youtube":
                    Picasso.get().load(Setting.YOUTUBE_IMAGE_FRONT + item.getVideo_id() + Setting.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.youtube).into(((MyViewHolder) holder).imageView);
                    break;
                case "dailymotion":
                    Picasso.get().load(Setting.DAILYMOTION_IMAGE_PATH + item.getVideo_id()).placeholder(R.drawable.dailymotion).into(((MyViewHolder) holder).imageView);
                    break;
                case "vimeo":
                    Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.vimeo).into(((MyViewHolder) holder).imageView);
                    break;
            }

            ((MyViewHolder) holder).cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickListener.onClick(holder.getAdapterPosition());
                }
            });

            ((MyViewHolder) holder).imageView_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionPopUp(((MyViewHolder) holder).imageView_option, holder.getAdapterPosition());
                }
            });


        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    if (Setting.isAdmobNativeAd) {
                        if (mNativeAdsAdmob.size() >= 5) {

                            int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                            UnifiedNativeAdView adView = (UnifiedNativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                            populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(adView);

                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                        }
                    } else {

                        LinearLayout ll_fb_native = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_fb, null);

                        com.facebook.ads.MediaView mvAdMedia;
                        AdIconView ivAdIcon;
                        TextView tvAdTitle;
                        TextView tvAdBody;
                        TextView tvAdSocialContext;
                        TextView tvAdSponsoredLabel;
                        Button btnAdCallToAction;
                        LinearLayout adChoicesContainer, ll_main;

                        mvAdMedia = ll_fb_native.findViewById(R.id.native_ad_media);
                        tvAdTitle = ll_fb_native.findViewById(R.id.native_ad_title);
                        tvAdBody = ll_fb_native.findViewById(R.id.native_ad_body);
                        tvAdSocialContext = ll_fb_native.findViewById(R.id.native_ad_social_context);
                        tvAdSponsoredLabel = ll_fb_native.findViewById(R.id.native_ad_sponsored_label);
                        btnAdCallToAction = ll_fb_native.findViewById(R.id.native_ad_call_to_action);
                        ivAdIcon = ll_fb_native.findViewById(R.id.native_ad_icon);
                        adChoicesContainer = ll_fb_native.findViewById(R.id.ad_choices_container);
                        ll_main = ll_fb_native.findViewById(R.id.ad_unit);


                        NativeAd ad;

                        if (mNativeAdsFB.size() >= 5) {
                            ad = mNativeAdsFB.get(new Random().nextInt(5));
                        } else {
                            ad = mNativeAdsManager.nextNativeAd();
                            mNativeAdsFB.add(ad);
                        }

                        ADViewHolder adHolder = (ADViewHolder) holder;

                        if (ad != null) {

                            tvAdTitle.setText(ad.getAdvertiserName());
                            tvAdBody.setText(ad.getAdBodyText());
                            tvAdSocialContext.setText(ad.getAdSocialContext());
                            tvAdSponsoredLabel.setText(ad.getSponsoredTranslation());
                            btnAdCallToAction.setText(ad.getAdCallToAction());
                            btnAdCallToAction.setVisibility(
                                    ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                            AdChoicesView adChoicesView = new AdChoicesView(context,
                                    ad, true);
                            adChoicesContainer.addView(adChoicesView, 0);

                            ArrayList<View> clickableViews = new ArrayList<>();
                            clickableViews.add(ivAdIcon);
                            clickableViews.add(mvAdMedia);
                            clickableViews.add(btnAdCallToAction);
                            ad.registerViewForInteraction(
                                    adHolder.itemView,
                                    mvAdMedia,
                                    ivAdIcon,
                                    clickableViews);

                            ((ADViewHolder) holder).rl_native_ad.addView(ll_fb_native);
                        }
                    }
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public String format(Number number) {
        char[] arrc = new char[]{' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long l = number.longValue();
        double d = l;
        int n = (int)Math.floor((double)Math.log10((double)d));
        int n2 = n / 3;
        if (n >= 3 && n2 < arrc.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new DecimalFormat("#0.0").format(d / Math.pow((double)10.0, (double)(n2 * 3))));
            stringBuilder.append(arrc[n2]);
            return stringBuilder.toString();
        }
        return new DecimalFormat("#,##0").format(l);
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    private void openOptionPopUp(ImageView imageView, final int pos) {
        Context wrapper = new ContextThemeWrapper(context, R.style.YOURSTYLE);
        PopupMenu popup = new PopupMenu(wrapper, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_song, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_youtube:
                        Intent intent = new Intent(Intent.ACTION_SEARCH);
                        intent.setPackage("com.google.android.youtube");
                        intent.putExtra("query", arrayList.get(pos).getVideo_title());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case R.id.popup_share:
                        methods.shareSong(arrayList.get(pos));
                        break;
                }
                return true;
            }
        });
        popup.show();
    }


    public int getRealPos(int pos, ArrayList<itemVideo> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
    }

    private void loadNativeAds() {
        if (Setting.isAdmobNativeAd) {
            AdLoader.Builder builder = new AdLoader.Builder(context, Setting.ad_native_id);
            adLoader = builder.forUnifiedNativeAd(
                    new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            // A native ad loaded successfully, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            mNativeAdsAdmob.add(unifiedNativeAd);
                            isAdLoaded = true;
                        }
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {

                        }
                    }).build();

            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        } else if (Setting.isFBNativeAd) {
            mNativeAdsManager = new NativeAdsManager(context, Setting.fb_ad_native_id, 5);
            mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    isAdLoaded = true;
                }

                @Override
                public void onAdError(AdError adError) {

                }
            });
            mNativeAdsManager.loadAds();
        }
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}