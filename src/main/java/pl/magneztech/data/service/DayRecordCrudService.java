package pl.magneztech.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.helpers.CrudService;
import pl.magneztech.data.entity.Record;

import java.time.LocalDate;
import java.util.List;

public final class DayRecordCrudService extends CrudService<Record, Integer> {

    private final RecordService recordService;
    private LocalDate date;

    public DayRecordCrudService(RecordService recordService, LocalDate date) {
        this.recordService = recordService;
        this.date = date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    protected RecordRepository getRepository() {
        return recordService.getRepository();
    }

    @Override
    public Page<Record> list(Pageable pageable) {
        return getRepository().findAllByDate(date, pageable);
    }

    @Override
    public int count() {
        return getRepository().countAllByDate(date);
    }

    public List<Record> findAll() {
        return getRepository().findAllByDate(date);
    }
}