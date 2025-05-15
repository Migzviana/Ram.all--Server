package com.example.Ramal_back.infra.service;

import com.example.Ramal_back.domain.extensions.ExtensionRange;
import com.example.Ramal_back.repositories.RangeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RangeService {
    private final RangeRepository repository;
    private ExtensionRange range;

    @PostConstruct
    public void init() {
        range = repository.findAll().stream().findFirst()
                .orElseGet(() -> repository.save(
                        ExtensionRange.builder().startRange(1000).endRange(1999).build()
                ));
    }

    public void setRange(int start, int end) {
        range.setStartRange(start);
        range.setEndRange(end);
        repository.save(range);
    }

    public boolean isInRange(String extensionNumber) {
        try {
            int number = Integer.parseInt(extensionNumber);
            return number >= range.getStartRange() && number <= range.getEndRange();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getStart() {
        return range.getStartRange();
    }

    public int getEnd() {
        return range.getEndRange();
    }
}