package com.example.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;
    
    @NotEmpty(message="키워드 혹은 해시태그를 입력해주세요.")
    private String value;
}