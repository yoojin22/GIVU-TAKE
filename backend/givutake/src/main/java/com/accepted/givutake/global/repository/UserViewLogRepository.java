package com.accepted.givutake.global.repository;

import com.accepted.givutake.global.entity.UserViewLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserViewLogRepository extends JpaRepository<UserViewLogs, Long> {
}
