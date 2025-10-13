package com.karunamay.airlink.repository.user;

import com.karunamay.airlink.model.user.Permission;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Hidden
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    List<Permission> findByAction(String action);

    List<Permission> findByResource(String resource);

    Optional<Permission> findByResourceAndAction(String resource, String action);

    boolean existsByName(String username);

    @Query("SELECT DISTINCT p.resource FROM Permission p WHERE p.resource IS NOT NULL ORDER BY p.resource")
    List<String> findAllResources();

    @Query("SELECT DISTINCT p.action FROM Permission p WHERE p.action IS NOT NULL ORDER BY p.action")
    List<String> findAllActions();

    Set<Permission> findByIdIn(Set<Long> ids);

    Set<Permission> findByNameIn(Set<String> names);

}
