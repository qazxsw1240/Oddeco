package org.hansung.oddeco.core.entity.career;

public interface CareerPermissionBuilder {
    public abstract CareerPermissionBuilder setCareer(Career career);

    public abstract CareerPermissionBuilder setId(long id);

    public abstract CareerPermissionBuilder setId(String id);

    public abstract CareerPermissionBuilder setName(String name);

    public abstract CareerPermissionBuilder setDescription(String description);

    public abstract CareerPermission build();
}
