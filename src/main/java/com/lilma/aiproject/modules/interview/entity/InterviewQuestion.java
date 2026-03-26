package com.lilma.aiproject.modules.interview.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_question")
@Data
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Integer questionOrder;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false,length = 32)
    private String sourceType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        if (this.sourceType==null)this.sourceType="SYSTEM";
    }
}
