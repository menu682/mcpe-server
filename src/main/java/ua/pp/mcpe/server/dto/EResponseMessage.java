package ua.pp.mcpe.server.dto;

public enum EResponseMessage {

    /**
     * Response message enum
     */

    REGISTER_SUCCESSFULLY("User registered successfully!"),
    CATEGORY_DELETED("Category deleted"),
    CATEGORY_IS_EMPTY("Category is empty"),
    VERSION_DELETED("Version deleted"),
    MOD_DELETED("Mod deleted");

    private String message;

    EResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
