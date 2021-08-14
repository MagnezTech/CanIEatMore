package pl.magneztech.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import pl.magneztech.data.service.EntryRepository;
import pl.magneztech.data.entity.Entry;
import pl.magneztech.data.service.RecordRepository;
import pl.magneztech.data.entity.Record;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(EntryRepository entryRepository, RecordRepository recordRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (entryRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Entry entities...");
            ExampleDataGenerator<Entry> entryRepositoryGenerator = new ExampleDataGenerator<>(Entry.class,
                    LocalDateTime.of(2021, 8, 14, 0, 0, 0));
            entryRepositoryGenerator.setData(Entry::setId, DataType.ID);
            entryRepositoryGenerator.setData(Entry::setName, DataType.WORD);
            entryRepositoryGenerator.setData(Entry::setKcal, DataType.NUMBER_UP_TO_100);
            entryRepositoryGenerator.setData(Entry::setFat, DataType.NUMBER_UP_TO_100);
            entryRepositoryGenerator.setData(Entry::setCarbohydrate, DataType.NUMBER_UP_TO_100);
            entryRepositoryGenerator.setData(Entry::setProtein, DataType.NUMBER_UP_TO_100);
            entryRepository.saveAll(entryRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Record entities...");
            ExampleDataGenerator<Record> recordRepositoryGenerator = new ExampleDataGenerator<>(Record.class,
                    LocalDateTime.of(2021, 8, 14, 0, 0, 0));
            recordRepositoryGenerator.setData(Record::setId, DataType.ID);
            recordRepositoryGenerator.setData(Record::setEntry, DataType.WORD);
            recordRepositoryGenerator.setData(Record::setWeight, DataType.NUMBER_UP_TO_100);
            recordRepositoryGenerator.setData(Record::setDate, DataType.DATE_OF_BIRTH);
            recordRepository.saveAll(recordRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}