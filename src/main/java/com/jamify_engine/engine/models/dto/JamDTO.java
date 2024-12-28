package com.jamify_engine.engine.models.dto;

import com.jamify_engine.engine.models.enums.JamStatusEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JamDTO extends MainDTO {
    String name;
    Long hostId;
    JamStatusEnum status;
    List<String> themes; // List<Tag> ?
    List<Long> participants;
    List<JamMessageDTO> messages;
}
