package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.common.enums.WaitlistStatus;
import com.movein.shuttlesync.entity.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<WaitlistEntry, Long> {
    List<WaitlistEntry> findByTripIdAndStatusOrderByPriorityOrderAsc(Long tripId, WaitlistStatus status);
    List<WaitlistEntry> findByUserId(Long userId);
    int countByTripIdAndStatus(Long tripId, WaitlistStatus status);
}
