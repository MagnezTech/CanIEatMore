package pl.magneztech.data.entity;

import pl.magneztech.data.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.time.LocalDate;

@Entity
public class Record extends AbstractEntity {

    @ManyToOne(optional = false)
    private Entry entry;
    @Column(nullable = false)
    private Integer weight;
    @Column(nullable = false)
    private LocalDate date;

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
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

    @Transient
    public Double getKcal() {
        return weight * entry.getKcal() / 100;
    }

    @Transient
    public Double getFat() {
        return weight * entry.getFat() / 100;
    }

    @Transient
    public Double getCarbohydrate() {
        return weight * entry.getCarbohydrate() / 100;
    }

    @Transient
    public Double getProtein() {
        return weight * entry.getProtein() / 100;
    }
}
