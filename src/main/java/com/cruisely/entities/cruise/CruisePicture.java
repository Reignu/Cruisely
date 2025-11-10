package com.cruisely.entities.cruise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Entity(name = "cruise_pictures")
public class CruisePicture extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "CRUISE_PICTURE_SEQ_GEN", sequenceName = "cruise_pictures_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRUISE_PICTURE_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "img")
    private String img;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "img_name")
    private String imgName;

    public CruisePicture(@NotNull(message = CONSTRAINT_NOT_NULL) String img, @NotNull(message = CONSTRAINT_NOT_NULL) String imgName) {
        this.img = img;
        this.imgName = imgName;
    }

    public CruisePicture() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}