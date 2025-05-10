package ru.rtln.userservice.repository.base;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendedRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

    private final EntityManager entityManager;

    public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    @Override
    public void refresh(List<T> entity) {
        entity.forEach(entityManager::refresh);
    }

    /**
     * Update passed data and refresh the value
     *
     * @param entity value
     * @return updated value
     */
    @Override
    public T saveAndRefresh(T entity) {
        super.save(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        return entity;
    }

    @Override
    public List<T> saveAllAndRefresh(List<T> entity) {
        return entity.stream()
            .map(this::saveAndRefresh)
            .collect(Collectors.toList());
    }

    @Override
    public void flush() {
        entityManager.flush();
    }
}
