package com.vp.alf.component.user.utils;

import com.vp.alf.common.controls.AlfNeo4jQueryRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import user.enums.AlfUserProperty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vp.alf.component.user.utils.AlfUserTemplateTest.createNewProperties;
import static user.enums.AlfUserProperty.*;

@Component
@RequiredArgsConstructor
public class AlfTestUtils {

    private final AlfNeo4jQueryRunner runner;

    public void createUserProperty() {
        List<String> properties = Stream.of(NAME, EMAIL, PERMISSIONS, PASSWORD)
                .map(AlfUserProperty::toString)
                .collect(Collectors.toList());
        runner.writeQuery(createNewProperties(properties)).collectList().block();
    }

}
