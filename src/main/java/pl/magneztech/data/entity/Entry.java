package pl.magneztech.data.entity;

import pl.magneztech.data.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Entry extends AbstractEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double kcal;
    @Column(nullable = false)
    private Double fat;
    @Column(nullable = false)
    private Double carbohydrate;
    @Column(nullable = false)
    private Double protein;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getKcal() {
        return kcal;
    }

    public void setKcal(Double kcal) {
        this.kcal = kcal;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

}
