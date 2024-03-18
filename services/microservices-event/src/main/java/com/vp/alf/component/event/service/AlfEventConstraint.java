package com.vp.alf.component.event.service;

import event.AlfEventDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Component
public class AlfEventConstraint {

    public boolean validateBeforeUpsert(AlfEventDto eventDto, Set<AlfEventDto> notValidEvents, boolean isUpdate) {

        String cause = StringUtils.EMPTY;
        if (isUpdate) {
            cause = Objects.isNull(eventDto.getId()) ? String.join(", ", "missing event id") : cause;
        }
        cause = StringUtils.isEmpty(eventDto.getEventName()) ? String.join(", ", "missing event name") : cause;
        cause = Objects.isNull(eventDto.getStartAt()) ? String.join(", ", "missing event start date") : cause;
        if (Objects.nonNull(eventDto.getStartAt())) {
            cause = Date.from(Instant.now()).after(eventDto.getStartAt()) ? String.join(", ", "can't set past time") : cause;
        }
        cause = StringUtils.isEmpty(eventDto.getLocation()) ? String.join(", ", "missing event location") : cause;
        cause = StringUtils.isEmpty(eventDto.getVenue()) ? String.join(", ", "missing event venue") : cause;

        if (StringUtils.isNotEmpty(cause)) {
            eventDto.setCause(cause);
            notValidEvents.add(eventDto);
            return false;
        } else
            return true;
    }
}
