package org.hansung.oddeco.core.entity.career;

class CareerPermissionBuilderImpl implements CareerPermissionBuilder {
    private volatile Career career;
    private volatile long id;
    private volatile String name;
    private volatile String description;

    public CareerPermissionBuilderImpl() {
        this.career = null;
        this.id = 0L;
        this.name = null;
        this.description = null;
    }

    @Override
    public CareerPermissionBuilder setCareer(Career career) {
        this.career = career;
        return this;
    }

    @Override
    public CareerPermissionBuilder setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    public CareerPermissionBuilder setId(String id) {
        this.id = Long.parseLong(id);
        return this;
    }

    @Override
    public CareerPermissionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CareerPermissionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public CareerPermission build() {
        if (this.career == null) {
            throw new IllegalStateException("career not set");
        }
        if (this.name == null) {
            throw new IllegalStateException("name not set");
        }
        if (this.description == null) {
            throw new IllegalStateException("description not set");
        }
        return new CareerPermissionImpl(this.career, this.id, this.name, this.description);
    }
}
