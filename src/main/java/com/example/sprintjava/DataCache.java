package com.example.sprintjava;

import com.example.entity.Produto;

import java.util.HashMap;
import java.util.Map;

public class DataCache {
    private static final Map<Long, Produto> productCache = new HashMap<>();

    public static Produto getProductById(Long productId) {
        return productCache.get(productId);
    }

    public static void cacheProduct(Produto product) {
        productCache.put(product.getId(), product);
    }
}
