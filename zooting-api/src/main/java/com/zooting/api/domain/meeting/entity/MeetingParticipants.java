package com.zooting.api.domain.meeting.entity;

import com.zooting.api.domain.BaseEntity;
import com.zooting.api.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "meeting_participants")
public class MeetingParticipants extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meeting_participants_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "meeting_log_id")
    private  MeetingLog meetingId;

    @OneToOne
    private Member participant1;

    @OneToOne
    private Member participant2;

    @OneToOne
    private Member participant3;

}
