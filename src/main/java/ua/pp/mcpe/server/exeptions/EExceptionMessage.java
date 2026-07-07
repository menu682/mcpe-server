package ua.pp.mcpe.server.exeptions;

public enum EExceptionMessage {
    /**
     * Exception messages enum
     */
    CATEGORY_NO_SUCH("Error: no such category"),
    CATEGORY_ALREADY_EXISTS("Error: this category already exists"),
    CATEGORY_IS_PARENT("Error: You cannot delete a category, it is a parent"),
    CATEGORY_IS_NOT_EMPTY("Error: Category is not empty. Can not be deleted!"),
    CATEGORY_CAN_NOT_BE_NULL("Error: Category id can not be null"),
    UNAUTHORIZED("Error: Unauthorized"),
    USER_NOT_FOUND("User Not Found with username: "),
    FIELDS_MUST_NOT_BE_EMPTY("Fields must not be empty"),
    NAME_IS_ALREADY_TAKEN("This name is already taken, please try another one."),
    EMAIL_IS_ALREADY_TAKEN("This email is already taken, please try another one."),
    NO_SUCH_ROLE("No such role"),
    MOD_NOT_FOUND("Mod not found"),
    MOD_ALREADY_EXISTS("Error: this mod already exists"),
    MOD_PUT_ACCESS_IS_DENIED("Access denied, only the user who created the ad can change it!"),
    MOD_DELETE_ACCESS_IS_DENIED("Access denied, only the user who created the ad can delete it!"),
    UNSUPPORTED_FILE_FORMAT("Unsupported file format"),
    FILE_NOT_FOUND("File not found"),
    VERSION_NOT_FOUND("Version not found"),
    FILE_NOT_MACH_VERSION("The requested file does not match the version"),
    VERSION_CONTAINS_FILES("You cannot delete a version. Some files contains this version."),
    VERSION_ALREADY_EXISTS("Error: this version already exists"),
    INVALID_FILE_NAME("Assert: File name invalid or empty!"),
    CONFIG_NOT_CHANGE("Configuration has not changed!"),
    PHOTO_NOT_FOUND("Photo not found");



    private String message;
    EExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
