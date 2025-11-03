package com.tuogo.cicd;

import com.tuogo.store.application.internal.commandservices.StoreCommandService;
import com.tuogo.store.application.internal.queryservices.StoreQueryService;
import com.tuogo.store.domain.model.commands.DeleteStoreCommand;
import com.tuogo.store.domain.model.commands.SaveStoreCommand;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TuogoDemoApplicationTests {

    @Resource
    private StoreCommandService storeCommandService;
    @Resource
    private StoreQueryService storeQueryService;

    private static SaveStoreCommand command;

    @BeforeAll
    static void beforeTest() {
        command = new SaveStoreCommand();
        command.setUidPk("STOBY00001");
        command.setSapCode("500001");
        command.setTaxNr("100000001");
        command.setTitle("Demo Store 001");
    }

    @Test
    @Order(1)
    @DisplayName("Context Load Test")
    void contextLoads() {}

    @Test
    @Order(2)
    @DisplayName("Create a new store data")
    void createStore() {
        storeCommandService.save(command);
    }

    @Test
    @Order(3)
    @DisplayName("Find store by ID")
    void findById() {
        Assertions.assertNotNull(command, "Store ID should not be null");
        String storeId = command.getUidPk();
        var store = storeQueryService.findByPK(storeId);
        Assertions.assertNotNull(store, "Cannot find store by ID: " + storeId);
        Assertions.assertEquals(command.getTitle(), store.getTitle());
        Assertions.assertEquals(command.getTaxNr(), store.getTaxNr());
        Assertions.assertEquals(command.getSapCode(), store.getSapCode());
    }

    @Test
    @Order(4)
    @DisplayName("Delete store by ID")
    void deleteStore() {
        Assertions.assertNotNull(command, "Store ID should not be null");
        String storeId = command.getUidPk();
        storeCommandService.delete(new DeleteStoreCommand(storeId));
        var store = storeQueryService.findByPK(storeId);
        Assertions.assertNull(store, "Store should be deleted with ID: " + storeId);
    }
}
