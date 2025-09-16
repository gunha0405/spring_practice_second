package com.example.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.answer.Answer;
import com.example.question.Question;
import com.example.user.SiteUser;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /* 질문 댓글 생성 */
    public Comment create(Question question, SiteUser author, String content) {
        Comment c = new Comment();
        c.setContent(content);
        c.setCreateDate(LocalDateTime.now());
        c.setQuestion(question);   // 질문과 연결
        c.setAuthor(author);
        return this.commentRepository.save(c);
    }

    /* 답변 댓글 생성 */
    public Comment create(Answer answer, SiteUser author, String content) {
        Comment c = new Comment();
        c.setContent(content);
        c.setCreateDate(LocalDateTime.now());
        c.setAnswer(answer);       // 답변과 연결
        c.setAuthor(author);
        return this.commentRepository.save(c);
    }

    /* 댓글 조회 */
    public Optional<Comment> getComment(Integer id) {
        return this.commentRepository.findById(id);
    }

    /* 댓글 수정 */
    public Comment modify(Comment c, String content) {
        c.setContent(content);
        c.setModifyDate(LocalDateTime.now());
        return this.commentRepository.save(c);
    }

    /* 댓글 삭제 */
    public void delete(Comment c) {
        this.commentRepository.delete(c);
    }
    
    public List<Comment> getUserComments(SiteUser user) {
        return commentRepository.findByAuthor(user);
    }
}
