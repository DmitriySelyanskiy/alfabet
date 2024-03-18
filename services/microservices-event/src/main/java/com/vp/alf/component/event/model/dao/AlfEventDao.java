package com.vp.alf.component.event.model.dao;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "events")
@Builder
public class AlfEventDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column("event_name")
    private String eventName;
    @Column("created_at")
    private Long createdAt;
    @Column("start_at")
    private Long startAt;
    private String location;
    private String venue;
    private int participants;
    private boolean completed;
}
