package org.hansung.oddeco.core.entity.career;

class CareerPermissionImpl implements CareerPermission {
    private final Career career;
    private final long id;
    private final String name;
    private final String description;

    public CareerPermissionImpl(Career career, long id, String name, String description) {
        this.career = career;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public Career getCareer() {
        return this.career;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getIdAsString() {
        return String.valueOf(this.id);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
