package com.example.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user.SiteUser;

public interface CommentRepository extends JpaRepository<Comment, Integer>{

	List<Comment> findByAuthor(SiteUser user);

}
