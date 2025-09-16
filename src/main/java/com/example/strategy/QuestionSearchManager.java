package com.example.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.example.question.Question;
import com.example.util.TenantContext;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuestionSearchManager {
	
	private final Map<String, QuestionSearchStrategy> strategies;
	
	public Page<Question> findByUserInput(int page, String subject, String value) {
		String tenant = TenantContext.get();
		QuestionSearchStrategy strategy = strategies.get(tenant);
		if (strategy == null) {
            throw new IllegalStateException("Unsupported tenant: " + tenant);
        }
        return strategy.search(page, subject, value);
		
	}
	
}
