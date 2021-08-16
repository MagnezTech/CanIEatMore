package pl.magneztech.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.magneztech.data.entity.Record;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    Page<Record> findAllByDate(LocalDate date, Pageable pageable);

    List<Record> findAllByDate(LocalDate date);

    int countAllByDate(LocalDate date);
}