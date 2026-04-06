package bds;

public record Permission(String name, String resource, String description) {
    public Permission (String name, String resource, String description){
        if (name.matches("^.*\\s.*$")){
            throw new IllegalArgumentException("Поле name имеет пробелы");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Поле description пустое");
        }

        String upperName = name.toUpperCase();
        String lowerResource = resource.toLowerCase();

        this.name = upperName;
        this.resource = lowerResource;
        this.description = description;
    }

    public static Permission create(String name, String resource, String description){
        return new Permission(name, resource, description);
    }

    public String format(){
        return String.format("%s on %s: %s", name, resource, description);
    }

    public boolean matches(String namePattern, String resourcePattern){
        boolean name_matches = name.contains(namePattern);
        boolean resource_matches = resource.contains(resourcePattern);

        return name_matches && resource_matches;
    }

}
