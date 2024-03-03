package com.enigma.wmb_api_next.repository;

import com.enigma.wmb_api_next.entity.TransType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransTypeRepository extends JpaRepository<TransType, String> {

    @Query(value = "SELECT * FROM m_trans_type WHERE id = :id", nativeQuery = true)
    Optional<TransType> findTransTypeEnumById(@Param("id") String id);
}
