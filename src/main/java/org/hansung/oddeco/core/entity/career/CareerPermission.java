package org.hansung.oddeco.core.entity.career;

public interface CareerPermission {
    public abstract Career getCareer();

    public abstract long getId();

    public abstract String getIdAsString();

    public abstract String getName();

    public abstract String getDescription();
}
