package services;

import org.junit.jupiter.api.BeforeEach;
import util.Managers;

public class InMemoryTaskManagerTest extends TaskManagerTest{

    @BeforeEach
    private void BeforeEach(){
        taskManager = Managers.getDefault();
    }

}
