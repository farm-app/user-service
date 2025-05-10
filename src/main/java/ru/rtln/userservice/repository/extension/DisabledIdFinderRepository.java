package ru.rtln.userservice.repository.extension;

import java.time.Duration;
import java.util.List;

public interface DisabledIdFinderRepository {

    List<Long> findAllDisabledIds(Duration lifetime);

    void deleteAllByIdInBatch(Iterable<Long> ids);
}
