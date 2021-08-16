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
    public Double kcal() {
        return weight * entry.getKcal();
    }

    @Transient
    public Double fat() {
        return weight * entry.getFat();
    }

    @Transient
    public Double carbohydrate() {
        return weight * entry.getCarbohydrate();
    }

    @Transient
    public Double protein() {
        return weight * entry.getProtein();
    }
}
