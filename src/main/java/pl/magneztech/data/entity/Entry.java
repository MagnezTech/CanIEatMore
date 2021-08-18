package pl.magneztech.data.entity;

import lombok.Data;
import pl.magneztech.data.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
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

}
