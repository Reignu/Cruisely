package com.cruisely.entities.auth.accesslevels;

import lombok.ToString;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.AccessLevelType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "administrators")
@DiscriminatorValue("Administrator")
@ToString
public class Administrator extends AccessLevel {

    @Override
    public AccessLevelType getAccessLevelType() {
        return AccessLevelType.ADMINISTRATOR;
    }

    public Administrator() {
        this.enabled = false;
    }

    public Administrator(boolean enabled) {
        this.enabled = enabled;
    }

}