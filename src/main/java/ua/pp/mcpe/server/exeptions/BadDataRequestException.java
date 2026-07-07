package ua.pp.mcpe.server.exeptions;


public class BadDataRequestException extends RuntimeException{

    public BadDataRequestException(String message){
        super(message);
    }
}

