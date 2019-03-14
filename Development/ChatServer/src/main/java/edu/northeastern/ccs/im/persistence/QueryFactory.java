package edu.northeastern.ccs.im.persistence;

public class QueryFactory {
    private static IQueryHandler iQueryHandler = null;

    public static IQueryHandler getQueryHandler() {
        if (iQueryHandler == null) {
            iQueryHandler = new QueryHandlerMySQLImpl();
        }
        return iQueryHandler;
    }
}
