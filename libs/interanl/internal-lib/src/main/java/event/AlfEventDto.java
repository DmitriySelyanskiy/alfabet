package event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AlfEventDto {
    private Long id;
    private String eventName;
    private Date createdAt;
    private Date startAt;
    private String location;
    private String venue;
    private int participants;
    private String cause;
}
