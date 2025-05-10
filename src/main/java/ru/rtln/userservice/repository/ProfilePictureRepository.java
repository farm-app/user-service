package ru.rtln.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import ru.rtln.userservice.entity.ProfilePicture;
import ru.rtln.userservice.repository.extension.DisabledIdFinderRepository;

import java.time.Duration;
import java.util.List;

public interface ProfilePictureRepository extends
        JpaRepository<ProfilePicture, Long>, DisabledIdFinderRepository {

    @Query("""
             SELECT  p.id
             FROM   ProfilePicture p
             WHERE  NOT p.active OR
                    (p.user IS NULL AND CURRENT_TIMESTAMP - p.createdAt > :lifetime)
            """)
    List<Long> findAllDisabledIds(Duration lifetime);

    @Override
    @Query("SELECT EXISTS(SELECT 1 FROM ProfilePicture p WHERE p.id = :id AND p.active)")
    boolean existsById(@NonNull Long id);

    @Modifying
    @Query(value = """
            UPDATE ProfilePicture pp
             SET pp.active = FALSE, pp.user = NULL
             WHERE pp.id = :id
            """)
    void disableById(Long id);
}