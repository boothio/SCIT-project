package net.scit.sec.spring7.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.sec.spring7.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

}
