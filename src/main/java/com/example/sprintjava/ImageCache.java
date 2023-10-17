package com.example.sprintjava;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static final Map<String, Image> imageCache = new HashMap<>();

    public static Image getImageByURL(String imageURL) {
        return imageCache.get(imageURL);
    }

    public static void cacheImage(String imageURL) {
        Image image = new Image(imageURL, true);
        imageCache.put(imageURL, image);
    }
}
