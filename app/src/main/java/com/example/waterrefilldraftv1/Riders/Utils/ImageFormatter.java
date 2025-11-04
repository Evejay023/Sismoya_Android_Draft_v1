package com.example.waterrefilldraftv1.Riders.Utils;

import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.waterrefilldraftv1.R;

import android.widget.ImageView;

public class ImageFormatter {

    private static final String TAG = "ImageFormatter";
    private static final String BASE_IMAGE_HOST = "https://sismoya.bsit3b.site/";

    /**
     * Formats and loads gallon images safely
     */
    public static void loadGallonImage(ImageView imageView, String imagePath, int placeholderResId) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imageView.setImageResource(placeholderResId);
            return;
        }

        String fullImageUrl = formatImageUrl(imagePath);
        Log.d(TAG, "Loading gallon image: " + fullImageUrl);

        // ✅ SAFE APPROACH: Use simple Glide without complex transformations
        Glide.with(imageView.getContext())
                .load(fullImageUrl)
                .apply(new RequestOptions()
                        .placeholder(placeholderResId)
                        .error(placeholderResId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform() // ✅ Important: Prevent video detection
                        .dontAnimate()   // ✅ Important: No animations for static images
                )
                .into(imageView);
    }

    /**
     * Formats the image URL properly
     */
    public static String formatImageUrl(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        String path = imagePath.trim();

        // If already full URL, return as is
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        // Build proper URL
        if (path.startsWith("/")) {
            return BASE_IMAGE_HOST + path.substring(1);
        } else {
            return BASE_IMAGE_HOST + path;
        }
    }

    /**
     * Gets the appropriate placeholder based on gallon type
     */
    public static int getGallonPlaceholder(String gallonName) {
        if (gallonName == null) {
            return R.drawable.img_slim_container; // default
        }

        String name = gallonName.toLowerCase();
        if (name.contains("slim")) {
            return R.drawable.img_slim_container;
        } else if (name.contains("round")) {
            return R.drawable.img_round_container; // Make sure you have this drawable
        } else if (name.contains("mini")) {
            return R.drawable.img_mini_container; // Make sure you have this drawable
        } else {
            return R.drawable.img_slim_container; // default
        }
    }

    /**
     * Safe image loading with fallback - use this in your adapters
     */
    public static void safeLoadGallonImage(ImageView imageView, String imageUrl, String gallonName) {
        int placeholder = getGallonPlaceholder(gallonName);
        loadGallonImage(imageView, imageUrl, placeholder);
    }
}