package lamart.lkvms.core.utilities.exceptions;

public class UserIsNotInOrganizationException extends Exception{
    public UserIsNotInOrganizationException(){
        super("User is not a member of the selected organization");
    }
}
