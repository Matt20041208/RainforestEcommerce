package com.yulin.ecommerce.comment.service.impl;

import com.yulin.ecommerce.comment.entity.Comment;
import com.yulin.ecommerce.comment.mapper.CommentMapper;
import com.yulin.ecommerce.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public Comment createComment(Comment comment) {
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        commentMapper.insert(comment);
        return comment;
    }
    
    @Override
    public Comment updateComment(Long id, Comment comment) {
        Comment existingComment = getCommentById(id);
        existingComment.setContent(comment.getContent());
        existingComment.setRating(comment.getRating());
        existingComment.setUpdateTime(new Date());
        commentMapper.updateById(existingComment);
        return existingComment;
    }
    
    @Override
    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }
    
    @Override
    public Comment getCommentById(Long id) {
        // 这里应该添加 findById 方法到 CommentMapper
        // 暂时使用 findByProductId 或 findByUserId 替代
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentMapper.findByProductId(productId);
    }
    
    @Override
    public List<Comment> getCommentsByProductIdAndRating(Long productId, Integer rating) {
        // 暂时不实现
        return commentMapper.findByProductId(productId);
    }
    
    @Override
    public Comment replyComment(Long commentId, String reply) {
        // 暂时不实现
        return null;
    }
    
    @Override
    public Double getAverageRatingByProductId(Long productId) {
        // 暂时返回固定值
        return 5.0;
    }
    
    @Override
    public List<Object[]> getRatingDistributionByProductId(Long productId) {
        // 暂时不实现
        return null;
    }
}