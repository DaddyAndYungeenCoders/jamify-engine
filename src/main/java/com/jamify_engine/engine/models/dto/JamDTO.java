package com.jamify_engine.engine.models.dto;

import com.jamify_engine.engine.models.enums.JamStatusEnum;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    Set<String> themes; // List<Tag> ?
    Set<Long> participants;
    Set<Long> messages;
}
