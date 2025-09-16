package com.example.strategy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.example.question.Question;
import com.example.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Component("B")
@RequiredArgsConstructor
public class HashtagSearchStrategy implements QuestionSearchStrategy{
	private final QuestionRepository questionRepository;

    @Override 
    public String tenantKey() { 
    	return "B"; 
    }
    

    @Override
    public Page<Question> search(int page, String subject, String value) {
    	List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findBySubjectOrHashtag(subject, value, pageable);
    }
}
