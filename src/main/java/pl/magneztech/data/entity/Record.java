package pl.magneztech.data.entity;

import javax.persistence.Entity;

import pl.magneztech.data.AbstractEntity;
import java.time.LocalDate;

@Entity
public class Record extends AbstractEntity {

    private String entry;
    private Integer weight;
    private LocalDate date;

    public String getEntry() {
        return entry;
    }
    public void setEntry(String entry) {
        this.entry = entry;
    }
    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
