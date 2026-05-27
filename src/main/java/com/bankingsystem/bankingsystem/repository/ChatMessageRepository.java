package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ChatMessage entity.
 * Handles database operations for chat history.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Find all chat messages for a specific user ordered by creation date (newest first)
     */
    Page<ChatMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find all chat messages for a specific user (all records)
     */
    List<ChatMessage> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find chat messages for a user within a date range
     */
    List<ChatMessage> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find messages by type
     */
    Page<ChatMessage> findByMessageTypeOrderByCreatedAtDesc(String messageType, Pageable pageable);
    
    /**
     * Count total messages for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Get chat statistics
     */
    @Query("SELECT COUNT(DISTINCT c.userId) FROM ChatMessage c")
    long countDistinctUsers();
    
    @Query("SELECT COUNT(c) FROM ChatMessage c")
    long countTotalMessages();
}
