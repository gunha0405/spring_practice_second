package com.example.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.DataNotFoundException;
import com.example.answer.Answer;
import com.example.answer.AnswerRepository;
import com.example.category.Category;
import com.example.strategy.QuestionSearchManager;
import com.example.user.SiteUser;
import com.example.util.TenantContext;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final AnswerRepository answerRepository;

    private final QuestionRepository questionRepository;
    
    private final QuestionSearchManager manager;


    public List<Question> getList() {
        return this.questionRepository.findAll();
    }
    
    public Question getQuestion(Integer id) {  
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
    

    public void create(String subject, String content, String value, SiteUser user, Category category) {
        String tenant = Optional.ofNullable(TenantContext.get())
                                .orElseThrow(() -> new IllegalStateException("No tenant"));

        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(user);
        q.setCategory(category);
        q.setCreateDate(LocalDateTime.now());

        if ("A".equalsIgnoreCase(tenant)) {
            q.setKeyword(value);
            q.setHashtag(null);
        } else if ("B".equalsIgnoreCase(tenant)) {
            q.setHashtag(value);
            q.setKeyword(null);
        } else {
            throw new IllegalStateException("Unsupported tenant: " + tenant);
        }

        this.questionRepository.save(q);
    }
    
    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }
    
    public void modify(Question question, String subject, String content, String value, Category category) {
        String tenant = Optional.ofNullable(TenantContext.get())
                                .orElseThrow(() -> new IllegalStateException("No tenant"));

        question.setSubject(subject);
        question.setContent(content);
        question.setCategory(category);
        question.setModifyDate(LocalDateTime.now());

        if ("A".equalsIgnoreCase(tenant)) {
            question.setKeyword(value);
            question.setHashtag(null);
        } else if ("B".equalsIgnoreCase(tenant)) {
            question.setHashtag(value);
            question.setKeyword(null);
        } else {
            throw new IllegalStateException("Unsupported tenant: " + tenant);
        }

        this.questionRepository.save(question);
    }
    
    public void delete(Question question) {
        this.questionRepository.delete(question);
    }
    
    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }
    
    public Page<Question> search(int page, String subject, String value) {
        Page<Question> paging = manager.findByUserInput(page, subject, value);
        return paging;
    }
    
    public List<Question> getUserQuestions(SiteUser user) {
        return questionRepository.findByAuthor(user);
    }
    
}