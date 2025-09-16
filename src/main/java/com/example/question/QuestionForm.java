package com.example.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "카테고리를 선택하세요")
    private Integer categoryId;
    
    @NotEmpty(message="키워드 혹은 해시태그를 입력해주세요.")
    private String value;
}