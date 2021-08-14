package pl.magneztech.data.service;

import pl.magneztech.data.entity.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class EntryService extends CrudService<Entry, Integer> {

    private EntryRepository repository;

    public EntryService(@Autowired EntryRepository repository) {
        this.repository = repository;
    }

    @Override
    protected EntryRepository getRepository() {
        return repository;
    }

}
