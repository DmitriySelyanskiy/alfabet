package event;

import event.enums.AlfPropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AlfEventContext {
    private Set<Long> eventIds;
    private String location;
    private String venue;
    private AlfPropertyType type;
}
