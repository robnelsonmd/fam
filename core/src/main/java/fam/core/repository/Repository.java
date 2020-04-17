package fam.core.repository;

import fam.core.util.ClassUtil;

import java.util.List;

public interface Repository<E> {
    List<E> findAll();
    E save(E entity);

    @SuppressWarnings("unchecked")
    default Class<E> getEntityClass() throws ClassNotFoundException {
        return (Class<E>) ClassUtil.getEntityClass(getClass());
    }

    default String getEntityName() throws ClassNotFoundException {
        return ClassUtil.getEntityClass(getClass()).getSimpleName().toLowerCase();
    }
}
