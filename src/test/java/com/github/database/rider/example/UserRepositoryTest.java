package com.github.database.rider.example;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.example.dao.UserRepository;
import com.github.database.rider.junit5.DBUnitExtension;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import org.apache.openejb.junit5.ApplicationComposerExtension;
import org.apache.openejb.testing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;

@ExtendWith({ApplicationComposerExtension.class, DBUnitExtension.class})
@Default
@Classes(cdi = true)
@Descriptors(@Descriptor(name = "persistence.xml", path = "META-INF/persistence.xml"))
@ContainerProperties({
        @ContainerProperties.Property(name = "jakarta.persistence.provider", value="org.eclipse.persistence.jpa.PersistenceProvider"),
        @ContainerProperties.Property(name = "jdbc/user", value = "new://Resource?type=DataSource"),
        @ContainerProperties.Property(name = "jdbc/user.LogSql", value = "true")
})
@DataSet(cleanBefore = true)
public class UserRepositoryTest {


    @Resource
    private DataSource dataSource;

    @Inject
    private UserRepository repository;

    private ConnectionHolder connectionHolder = () -> dataSource.getConnection();

    @Test
    @DataSet("datasets/users.yml")
    public void find1() {
        Assertions.assertEquals("John Smith", repository.find(1L).getName());
        Assertions.assertEquals("Clark Kent", repository.find(2L).getName());
    }

    @Test
    public void find2() { // ensure we didn't leak previous dataset
        Assertions.assertNull(repository.find(1L));
    }
}
