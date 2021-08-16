package pl.magneztech.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import pl.magneztech.data.entity.Entry;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class EntryService extends CrudService<Entry, Integer> {

    private final EntryRepository repository;

    public EntryService(@Autowired EntryRepository repository) {
        this.repository = repository;
    }

    @Override
    protected EntryRepository getRepository() {
        return repository;
    }

    public Collection<Entry> getAllEntries() {
        List<Entry> entries = getRepository().findAll();
        entries.sort(Comparator.comparing(Entry::getName));
        return entries;
    }
}
