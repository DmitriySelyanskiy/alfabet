package com.vp.alf.component.event.job;

import com.vp.alf.component.event.service.AlfEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlfEventCloseJob implements Job {

    private final AlfEventService eventService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey key = context.getJobDetail().getKey();
        String eventId = key.getName();

        eventService.closeEvent(Long.valueOf(eventId))
                .then()
                .subscribe();
    }
}
