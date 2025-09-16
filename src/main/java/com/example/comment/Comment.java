package com.example.comment;
import java.time.LocalDateTime;

import com.example.answer.Answer;
import com.example.question.Question;
import com.example.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment_table") 
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content", columnDefinition = "CLOB")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;
    
    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;
    
    public Integer getQuestionId() {
        Integer result = null;
        if (this.question != null) {
            result = this.question.getId();
        } else if (this.answer != null) {
            result = this.answer.getQuestion().getId();
        }
        return result;
    }
}