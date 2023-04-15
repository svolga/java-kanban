package services;

public class Manager {
    int nextId = 1;

    protected int getNextId() {
        return nextId++;
    }
}
