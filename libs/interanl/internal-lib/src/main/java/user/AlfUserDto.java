package user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user.enums.AlfUserProperty;

import java.util.EnumMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlfUserDto {
    private String userId;
    private EnumMap<AlfUserProperty, Object> userProperty;
}
