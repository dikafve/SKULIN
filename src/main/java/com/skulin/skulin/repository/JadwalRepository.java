package com.skulin.skulin.repository;

import com.skulin.skulin.model.Jadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JadwalRepository extends JpaRepository<Jadwal, Long> {
}