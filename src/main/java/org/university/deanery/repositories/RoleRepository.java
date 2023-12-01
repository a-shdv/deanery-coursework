package org.university.deanery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.university.deanery.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByTitle(String title);
}