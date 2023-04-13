package services;

public class Manager implements HasId{

    int nextId = 1;

    @Override
    public int getNextId() {
        return nextId++;
    }
}
