package fam.core.util;

import java.lang.reflect.ParameterizedType;

public class ClassUtil {
    @SuppressWarnings("unchecked")
    public static <E> Class<E> getEntityClass(Class<E> entityClass) throws ClassNotFoundException {
        ParameterizedType parameterizedType = ((ParameterizedType) entityClass.getGenericSuperclass());
        String className = parameterizedType.getActualTypeArguments()[0].getTypeName();
        return (Class<E>) Class.forName(className);
    }
}
