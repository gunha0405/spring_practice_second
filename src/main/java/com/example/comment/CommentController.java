package com.example.comment;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.answer.Answer;
import com.example.answer.AnswerService;
import com.example.question.Question;
import com.example.question.QuestionController;
import com.example.question.QuestionService;
import com.example.user.SiteUser;
import com.example.user.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final QuestionController questionController;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    CommentController(QuestionController questionController) {
        this.questionController = questionController;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/question/{id}")
    public String createQuestionComment(CommentForm commentForm) {
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String createQuestionComment(@PathVariable("id") Integer id,
                                        @Valid CommentForm commentForm,
                                        BindingResult bindingResult,
                                        Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment c = this.commentService.create(question, user, commentForm.getContent());
        return String.format("redirect:/question/detail/%s", c.getQuestionId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/answer/{id}")
    public String createAnswerComment(CommentForm commentForm) {
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createAnswerComment(@PathVariable("id") Integer id,
                                      @Valid CommentForm commentForm,
                                      BindingResult bindingResult,
                                      Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment c = this.commentService.create(answer, user, commentForm.getContent());
        return String.format("redirect:/question/detail/%s", c.getQuestionId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            commentForm.setContent(c.getContent());
            return "comment_form";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyComment(@Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal,
                                @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            c = this.commentService.modify(c, commentForm.getContent());
            return String.format("redirect:/question/detail/%s", c.getQuestionId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(Principal principal, @PathVariable("id") Integer id) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
            this.commentService.delete(c);
            return String.format("redirect:/question/detail/%s", c.getQuestionId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}
