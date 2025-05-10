package ru.rtln.userservice.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ExtendedRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    void refresh(T entity);

    void refresh(List<T> entity);

    T saveAndRefresh(T entity);

    List<T> saveAllAndRefresh(List<T> entity);

    void flush();
}

