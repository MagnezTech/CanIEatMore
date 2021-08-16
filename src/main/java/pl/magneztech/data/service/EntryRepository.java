package pl.magneztech.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.magneztech.data.entity.Entry;

public interface EntryRepository extends JpaRepository<Entry, Integer> {
}