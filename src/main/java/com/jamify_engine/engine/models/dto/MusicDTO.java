package com.jamify_engine.engine.models.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicDTO extends MainDTO {
    private Long id;
}
