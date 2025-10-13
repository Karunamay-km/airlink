package com.karunamay.airlink.repository.user;

import com.karunamay.airlink.model.user.Role;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :roleName")
    Optional<Role> findByNameWithPermission(@Param("roleName") String roleName);

    List<Role> findByActiveTrue();

    List<Role> findBySystemRoleTrue();

    boolean existsByName(String username);

}
