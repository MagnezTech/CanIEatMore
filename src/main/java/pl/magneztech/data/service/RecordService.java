package pl.magneztech.data.service;

import pl.magneztech.data.entity.Record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.time.LocalDate;

@Service
public class RecordService extends CrudService<Record, Integer> {

    private RecordRepository repository;

    public RecordService(@Autowired RecordRepository repository) {
        this.repository = repository;
    }

    @Override
    protected RecordRepository getRepository() {
        return repository;
    }

}
