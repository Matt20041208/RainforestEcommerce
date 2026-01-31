package com.yulin.ecommerce.comment.controller;

import com.yulin.ecommerce.comment.entity.Comment;
import com.yulin.ecommerce.comment.service.CommentService;
import com.yulin.ecommerce.product.mapper.ProductMapper;
import com.yulin.ecommerce.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    // 根据商品ID获取评论列表
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getCommentsByProductId(@PathVariable Long productId) {
        List<Comment> comments = commentService.getCommentsByProductId(productId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    // 添加评论
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long productId = ((Number) request.get("productId")).longValue();
            Integer rating = (Integer) request.get("rating");
            String content = (String) request.get("content");
            
            // 检查用户和商品是否存在
            if (userMapper.findById(userId) == null || productMapper.findById(productId) == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setProductId(productId);
            comment.setRating(rating);
            comment.setContent(content);
            
            Comment savedComment = commentService.createComment(comment);
            return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // 更新评论
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment existingComment = commentService.getCommentById(id);
        if (existingComment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        comment.setId(id);
        Comment updatedComment = commentService.updateComment(id, comment);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }
    
    // 删除评论
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // 商家回复评论
    @PostMapping("/{id}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable Long id, @RequestBody Map<String, String> request) {
        // 暂时不实现回复功能
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    // 获取商品平均评分
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Long productId) {
        // 暂时返回固定值
        return new ResponseEntity<>(Map.of("averageRating", 5.0), HttpStatus.OK);
    }
}