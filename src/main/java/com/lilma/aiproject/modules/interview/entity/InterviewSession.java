package com.lilma.aiproject.modules.interview.entity;

import com.lilma.aiproject.common.constant.InterviewSessionStatusConstants;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_session")
@Data
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resumeId;

    @Column(nullable = false,length=32)
    private String status;

    //总数量
    @Column(nullable = false)
    private Integer questionCount;

    //已经回答的数量，实现进度的概念
    @Column(nullable = false)
    private Integer answeredCount;

    @Column
    private Integer overallScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String finalSuggestion;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        LocalDateTime now=LocalDateTime.now();
        this.createdAt=now;
        this.updatedAt=now;

        if(this.status==null)this.status= InterviewSessionStatusConstants.CREATED;
        if(this.questionCount==null)this.questionCount=0;
        if(this.answeredCount==null)this.answeredCount=0;
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }

}
