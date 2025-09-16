package com.example.strategy;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.question.Question;

public interface QuestionSearchStrategy {
	
	String tenantKey();

	// value는 keyword 혹은 hashtag를 담음
	Page<Question> search(int page, String subject, String value);
	
}
