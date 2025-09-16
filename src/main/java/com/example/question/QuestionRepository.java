package com.example.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.user.SiteUser;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String content);
	List<Question> findBySubjectLike(String subject);
	
	Page<Question> findAll(Pageable pageable);

	@Query("""
		    SELECT q FROM Question q
		    WHERE q.subject LIKE %:kw% OR q.content LIKE %:kw%
		    ORDER BY q.createDate DESC
		""")
    Page<Question> findByKeyword(@Param("kw") String kw, Pageable pageable);

    @Query("""
    	    SELECT q FROM Question q
    	    LEFT JOIN q.answerList a
    	    GROUP BY q
    	    ORDER BY MAX(a.createDate) DESC NULLS LAST
    	""")
    Page<Question> findQuestionsOrderByLatestAnswer(Pageable pageable);

    @Query("""
    	    SELECT q FROM Question q
    	    LEFT JOIN q.commentList c
    	    GROUP BY q
    	    ORDER BY MAX(c.createDate) DESC NULLS LAST
    	""")
    Page<Question> findQuestionsOrderByLatestComment(Pageable pageable);
	
    Page<Question> findBySubjectAndKeyword(String subject, String keyword, Pageable pageable);
    
    Page<Question> findBySubjectOrHashtag(String subject, String hashtag, Pageable pageable);
    
	List<Question> findByAuthor(SiteUser user);
	
}
