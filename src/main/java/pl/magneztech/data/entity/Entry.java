package pl.magneztech.data.entity;

import javax.persistence.Entity;

import pl.magneztech.data.AbstractEntity;

@Entity
public class Entry extends AbstractEntity {

    private String name;
    private Integer kcal;
    private Integer fat;
    private Integer carbohydrate;
    private Integer protein;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getKcal() {
        return kcal;
    }
    public void setKcal(Integer kcal) {
        this.kcal = kcal;
    }
    public Integer getFat() {
        return fat;
    }
    public void setFat(Integer fat) {
        this.fat = fat;
    }
    public Integer getCarbohydrate() {
        return carbohydrate;
    }
    public void setCarbohydrate(Integer carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
    public Integer getProtein() {
        return protein;
    }
    public void setProtein(Integer protein) {
        this.protein = protein;
    }

}
