package com.example.user;

import java.security.Principal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.answer.Answer;
import com.example.answer.AnswerService;
import com.example.comment.Comment;
import com.example.comment.CommentService;
import com.example.question.Question;
import com.example.question.QuestionService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    
    private final QuestionService questionService;
    
    private final AnswerService answerService;
    
    private final CommentService commentService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), 
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getCustomerId());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
    
 // 비밀번호 찾기 (임시 비밀번호 발송)
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        try {
            String message = userService.sendTemporaryPassword(email);
            model.addAttribute("message", message);
        } catch (IllegalArgumentException | MessagingException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "forgot_password";
    }

    // 비밀번호 변경
    @GetMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePasswordForm() {
        return "change_password";
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, Model model) {
        try {
            userService.changePassword(principal.getName(),
                    currentPassword, newPassword, confirmPassword);
            model.addAttribute("message", "비밀번호가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "change_password";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());

        List<Question> questionList = questionService.getUserQuestions(user);
        List<Answer> answerList = answerService.getUserAnswers(user);
        List<Comment> commentList = commentService.getUserComments(user);

        model.addAttribute("user", user);
        model.addAttribute("questionList", questionList);
        model.addAttribute("answerList", answerList);
        model.addAttribute("commentList", commentList);

        return "profile";
    }
    
}
