package services;

import exception.ManagerSaveException;

public interface StorageManager {
    public void save() throws ManagerSaveException;
}
