package com.cruisely.entities.auth.accesslevels;

import lombok.ToString;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.AccessLevelType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "moderators")
@DiscriminatorValue("Moderator")
@ToString
public class Moderator extends AccessLevel {

    @Override
    public AccessLevelType getAccessLevelType() {
        return AccessLevelType.MODERATOR;
    }

    public Moderator() {
        this.enabled = false;
    }

    public Moderator(boolean enabled) {
        this.enabled = enabled;
    }
}