package com.yulin.ecommerce.comment.mapper;

import com.yulin.ecommerce.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findByProductId(Long productId);
    List<Comment> findByUserId(Long userId);
    int insert(Comment comment);
    int updateById(Comment comment);
    int deleteById(Long id);
}