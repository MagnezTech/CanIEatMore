package pl.magneztech.data.service;

import pl.magneztech.data.entity.Entry;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, Integer> {

}