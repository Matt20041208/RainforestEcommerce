package com.yulin.ecommerce.product.service.impl;

import com.yulin.ecommerce.product.entity.Product;
import com.yulin.ecommerce.product.mapper.ProductMapper;
import com.yulin.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @CacheEvict(value = {"products", "enabledProducts", "topSelling"}, allEntries = true)
    public Product createProduct(Product product) {
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        if (product.getEnabled() == null) {
            product.setEnabled(true);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        productMapper.insert(product);
        return product;
    }
    
    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = {"products", "enabledProducts", "searchResults", "topSelling"}, allEntries = true)
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImage(product.getImage());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setEnabled(product.getEnabled());
        existingProduct.setCategoryId(product.getCategoryId());
        existingProduct.setUpdateTime(new Date());
        productMapper.updateById(existingProduct);
        return existingProduct;
    }
    
    @Override
    @CacheEvict(value = {"product", "products", "enabledProducts", "searchResults", "topSelling", "filterResults"},
            key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        productMapper.deleteById(id);
    }
    
    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return product;
    }
    
    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        return productMapper.findByEnabled(null);
    }
    
    @Override
    @Cacheable(value = "products", key = "'enabled'")
    public List<Product> getEnabledProducts() {
        return productMapper.findByEnabled(true);
    }
    
    @Override
    @Cacheable(value = "products", key = "'category_'.concat(#categoryId.toString())")
    public List<Product> getProductsByCategory(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.findByPriceBetween(minPrice.doubleValue(), maxPrice.doubleValue());
    }
    
    @Override
    @Cacheable(value = "searchResults", key = "#keyword")
    public List<Product> searchProducts(String keyword) {
        return productMapper.findByNameContaining(keyword);
    }
    
    @Override
    @Cacheable(value = "topSelling", key = "#limit")
    public List<Product> getTopSellingProducts(Integer limit) {
        List<Product> products = productMapper.findTop10ByOrderBySalesDesc();
        if (limit != null && limit < products.size()) {
            return products.subList(0, limit);
        }
        return products;
    }
    
    @Override
    public org.springframework.data.domain.Page<Product> findProductsByFilters(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean enabled,
            String sortBy,
            Integer page,
            Integer pageSize) {
        
        // 1. 获取所有启用的商品
        List<Product> allProducts = this.getEnabledProducts();
        
        // 2. 应用筛选条件
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : allProducts) {
            boolean match = true;
            
            // 关键词筛选
            if (keyword != null && !keyword.isEmpty()) {
                if (!product.getName().toLowerCase().contains(keyword.toLowerCase()) &&
                    !product.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    match = false;
                }
            }
            
            // 分类筛选
            if (categoryId != null && !categoryId.equals(product.getCategoryId())) {
                match = false;
            }
            
            // 价格区间筛选
            if (minPrice != null && product.getPrice().compareTo(minPrice) < 0) {
                match = false;
            }
            if (maxPrice != null && product.getPrice().compareTo(maxPrice) > 0) {
                match = false;
            }
            
            // 如果所有条件都匹配，添加到结果列表
            if (match) {
                filteredProducts.add(product);
            }
        }
        
        // 3. 应用排序
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "price-asc":
                    filteredProducts.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "price-desc":
                    filteredProducts.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "sales-desc":
                    filteredProducts.sort(Comparator.comparing(Product::getSales).reversed());
                    break;
            }
        }
        
        // 4. 应用分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredProducts.size());
        List<Product> pagedProducts = new ArrayList<>();
        
        if (start < end && start >= 0 && end <= filteredProducts.size()) {
            pagedProducts = filteredProducts.subList(start, end);
        }
        
        // 5. 创建Page对象
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(
                pagedProducts,
                org.springframework.data.domain.PageRequest.of(page - 1, pageSize),
                filteredProducts.size()
        );
        
        return productPage;
    }
    
    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = {"products", "enabledProducts", "topSelling"}, allEntries = true)
    public Product updateStock(Long id, Integer stockChange) {
        Product product = getProductById(id);
        Integer newStock = product.getStock() + stockChange;
        if (newStock < 0) {
            throw new RuntimeException("库存不足");
        }
        product.setStock(newStock);
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
        return product;
    }
    
    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = {"products", "enabledProducts", "topSelling"}, allEntries = true)
    public Product updateSales(Long id, Integer salesChange) {
        Product product = getProductById(id);
        if (salesChange < 0) {
            throw new RuntimeException("销量不能减少");
        }
        product.setSales(product.getSales() + salesChange);
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
        return product;
    }

    @Override
    public int getTotalProducts() {
        return productMapper.findByEnabled(null).size();
    }
}