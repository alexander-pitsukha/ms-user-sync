package by.nestegg.user.migration.repository;

import by.nestegg.user.migration.AbstractTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.io.IOException;

public abstract class AbstractRepositoryTests<E> extends AbstractTests {

    @Autowired
    private TestEntityManager entityManager;

    protected E saveTestEntity(String fileSource, Class<E> valueType) throws IOException {
        E entity = getObjectFromJson(fileSource, valueType);
        saveTestEntity(entity);
        return entity;
    }

    protected void saveTestEntity(E entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

}
