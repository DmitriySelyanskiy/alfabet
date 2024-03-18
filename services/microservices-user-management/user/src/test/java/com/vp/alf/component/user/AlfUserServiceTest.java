package com.vp.alf.component.user;

import com.google.common.collect.Maps;
import com.vp.alf.commom.configuration.extensions.AlfNeo4jDBTestExtension;
import com.vp.alf.commom.database.AlfUserServiceTestBase;
import com.vp.alf.common.external_services.AlfEventRsocketService;
import com.vp.alf.component.event.service.AlfEventService;
import com.vp.alf.component.user.service.AlfUserService;
import com.vp.alf.component.user.utils.AlfTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import user.AlfUserDto;
import user.enums.AlfUserProperty;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static user.enums.AlfUserProperty.*;

@SpringBootTest
@ExtendWith({AlfNeo4jDBTestExtension.class})
public class AlfUserServiceTest  extends AlfUserServiceTestBase {

    @Autowired
    AlfUserService userService;

    @Autowired
    AlfEventService eventService;

    @Autowired
    AlfTestUtils useUtils;

    @MockBean
    AlfEventRsocketService eventRsocketService;

    @BeforeEach
    public void setUp() {
        useUtils.createUserProperty();
    }


    @Test
    void shouldCreateAndSubscribe() {
        AlfUserDto userDto = new AlfUserDto();
        EnumMap<AlfUserProperty, Object> property = Maps.newEnumMap(
                Map.of(
                        NAME, "Luke Skywalker",
                        EMAIL, "luke@mock.il",
                        PERMISSIONS, "ADMIN",
                        PASSWORD, "password"
                )
        );

        userDto.setUserProperty(property);

        eventService.createEvent(1L).block();

        Mockito.when(eventRsocketService.addParticipant(anyLong())).thenReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(
                userService.createUser(userDto)
                        .flatMap(userId -> userService.subscribeOnEvent(userId, 1L))
        )
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    void shouldDeleteEvent() {
        eventService.createEvent(1L).block();

        StepVerifier.create(eventService.deleteEvents(List.of(1L)))
                .verifyComplete();
    }

}
