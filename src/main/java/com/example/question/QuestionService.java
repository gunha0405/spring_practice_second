package com.example.question;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.DataNotFoundException;
import com.example.answer.AnswerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final AnswerRepository answerRepository;

    private final QuestionRepository questionRepository;


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
}