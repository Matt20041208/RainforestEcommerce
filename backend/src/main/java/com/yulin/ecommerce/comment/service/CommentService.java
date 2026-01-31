package com.yulin.ecommerce.comment.service;

import com.yulin.ecommerce.comment.entity.Comment;
import java.util.List;

public interface CommentService {
    // 添加评论
    Comment createComment(Comment comment);
    
    // 根据ID获取评论
    Comment getCommentById(Long id);
    
    // 根据商品ID获取评论列表
    List<Comment> getCommentsByProductId(Long productId);
    
    // 根据商品ID和评分获取评论列表
    List<Comment> getCommentsByProductIdAndRating(Long productId, Integer rating);
    
    // 更新评论
    Comment updateComment(Long id, Comment comment);
    
    // 删除评论
    void deleteComment(Long id);
    
    // 商家回复评论
    Comment replyComment(Long commentId, String reply);
    
    // 获取商品的平均评分
    Double getAverageRatingByProductId(Long productId);
    
    // 获取商品的评分分布
    List<Object[]> getRatingDistributionByProductId(Long productId);
}