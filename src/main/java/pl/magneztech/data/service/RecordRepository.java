package pl.magneztech.data.service;

import pl.magneztech.data.entity.Record;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface RecordRepository extends JpaRepository<Record, Integer> {

}