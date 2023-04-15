package repository;

import java.util.List;
import java.util.Map;

public interface Repo <T> {
    public T create (T t);
    public void update (T t);
    public void delete(int id);
    public T getById(int id);
    public List<T> getAll();
    public void clear();
}
