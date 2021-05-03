package glebi.helpers;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional get(String id);

    List<T> getAll();

    void insert(T t);

    void update(T t);

    void delete(T t) throws SQLIntegrityConstraintViolationException;
}
