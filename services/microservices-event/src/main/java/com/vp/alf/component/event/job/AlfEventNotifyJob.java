package com.vp.alf.component.event.job;

import com.vp.alf.common.external_services.AlfMailSenderNotification;
import com.vp.alf.component.event.model.dto.AlfNotificationDto;
import com.vp.alf.component.event.scheduler.AlfEventScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlfEventNotifyJob implements Job {

    @Qualifier("event-notification")
    private final Sinks.Many<AlfNotificationDto> sinkRequester;
    private final AlfMailSenderNotification mailSenderNotification;
    private final AlfEventScheduler eventScheduler;

    @Value("${app.timeInMinutes}")
    private int timeInMinutes;

    @Override
    public void execute(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        String eventId = key.getName();
        String eventName = key.getGroup();
        log.info("Starting job with key {} and group name {}.", eventId, eventName);
        log.info("Event {} starts in 30 minutes", eventName);

        // notify subscribers
        sinkRequester.tryEmitNext(AlfNotificationDto.builder()
                        .eventId(eventId)
                        .message(String.format("Event %s starts in %o minutes", eventName, timeInMinutes))
                .build());

        //   notify by mail sender not implemented
        mailSenderNotification.notify(eventId);

        eventScheduler.scheduleCloseEvent(eventId);

    }
}
