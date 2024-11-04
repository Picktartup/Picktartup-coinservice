package com.picktartup.coinservice.repository;

import com.picktartup.coinservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

}
