package event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlfEventUpsertResponse {
    private int upsert;
    private int notUpsert;
    private Set<AlfEventDto> notValidEvents;
}
