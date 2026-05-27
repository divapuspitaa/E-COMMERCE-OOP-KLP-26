package com.nusantarashop.service;

import com.nusantarashop.dao.OrderDAO;
import com.nusantarashop.dao.ProductDAO;
import com.nusantarashop.dao.UserDAO;
import com.nusantarashop.model.*;
import com.nusantarashop.util.SessionManager;

import java.util.List;

/**
 * ProductService - logika bisnis untuk manajemen produk.
 */
public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() { return productDAO.findAll(); }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllProducts();
        return productDAO.search(keyword.trim());
    }

    public List<Product> getByCategory(Product.Category category) {
        return productDAO.findByCategory(category);
    }

    public boolean saveProduct(Product product) {
        if (!product.isValid()) return false;
        return productDAO.save(product);
    }

    public boolean updateProduct(Product product) {
        if (!product.isValid()) return false;
        return productDAO.update(product);
    }

    public int countProducts() { return productDAO.countAll(); }
}

