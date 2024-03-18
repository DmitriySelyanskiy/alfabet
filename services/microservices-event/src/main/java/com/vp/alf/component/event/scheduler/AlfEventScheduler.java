package com.vp.alf.component.event.scheduler;

import com.vp.alf.component.event.job.AlfEventCloseJob;
import com.vp.alf.component.event.job.AlfEventNotifyJob;
import com.vp.alf.component.event.model.dao.AlfEventDao;
import event.AlfEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlfEventScheduler {

    private final Scheduler scheduler;

    @Value("${app.timeInMinutes}")
    private int timeInMinutes;

    public Mono<Long> scheduleNotifyEvent(AlfEventDao event) {
        JobDetail jobDetail = buildJobDetail(AlfEventNotifyJob.class, String.valueOf(event.getId()), event.getEventName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(Instant.ofEpochMilli(event.getStartAt())));
        calendar.add(Calendar.MINUTE, -timeInMinutes);
        Trigger trigger = buildTrigger(jobDetail, SimpleScheduleBuilder.simpleSchedule(), calendar.getTime());
        runScheduler(jobDetail, trigger);
        return Mono.just(event.getId());
    }

    private void runScheduler(JobDetail jobDetail, Trigger trigger) {
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to run {} schedule", jobDetail.getKey().getGroup());
            throw new RuntimeException(String.format("Failed to run %s schedule: Cause %s", jobDetail.getKey().getGroup(), e.getLocalizedMessage()));
        }
    }

    private JobDetail buildJobDetail(Class<? extends Job> job, String key, String groupName) {
        return JobBuilder.newJob(job)
                .withIdentity(key, groupName)
                .build();
    }

    private <T extends Trigger> Trigger buildTrigger(JobDetail jobDetail,
                                                     ScheduleBuilder<T> scheduleBuilder,
                                                     Date date) {
        JobKey key = jobDetail.getKey();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(key.getName(), key.getGroup())
                .startAt(date)
                .withSchedule(scheduleBuilder)
                .build();
    }

    public void unscheduleJob(String eventId, String eventName) {
        log.debug("Trying to unschedule a job with trigger ** event Id {} ** for event {}", eventId, eventName);
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(eventId, eventName));
            log.info("Job has unscheduled with trigger ** event Id {} ** for event {}", eventId, eventName);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleCloseEvent(String eventId) {
        JobDetail jobDetail = buildJobDetail(AlfEventCloseJob.class, eventId, "close");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, timeInMinutes);
        Trigger trigger = buildTrigger(jobDetail, SimpleScheduleBuilder.simpleSchedule(), calendar.getTime());
        runScheduler(jobDetail, trigger);
    }


    public Mono<Void> rescheduleJobIfNeeded(AlfEventDto event, Set<AlfEventDto> notValidEvents) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(event.getId()), event.getEventName());
            SimpleTriggerImpl trigger = (SimpleTriggerImpl) scheduler.getTrigger(triggerKey);

            Date nextFireTime = trigger.getNextFireTime();
            if (!nextFireTime.equals(event.getStartAt())) {
                nextFireTime = event.getStartAt();
                trigger.setNextFireTime(nextFireTime);
                scheduler.rescheduleJob(triggerKey, trigger.getTriggerBuilder()
                        .startAt(nextFireTime)
                        .build());
            }
        } catch (SchedulerException e) {
            notValidEvents.add(event);
            log.error("Scheduler with key {} doesn't exists", event.getId());
            throw new RuntimeException(e);
        }

        return Mono.empty();
    }
}
