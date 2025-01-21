package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.entities.TagEntity;
import com.jamify_engine.engine.repository.TagsRepository;
import com.jamify_engine.engine.service.interfaces.TagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepository tagsRepository;

    @Override
    public Optional<TagEntity> findByLabel(String label) {
        return tagsRepository.findByLabel(label);
    }

    @Override
    public TagEntity createNewTag(TagEntity tag) {
        return tagsRepository.save(tag);
    }
}
