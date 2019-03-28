package edu.northeastern.ccs.im.persistence;

/**
 * A factory for creating Query objects.
 */
public class QueryFactory {
    
    /** The Query handler. */
    private static IQueryHandler queryHandler = null;

    /**
     * Gets the query handler.
     *
     * @return the query handler
     */
    public static IQueryHandler getQueryHandler() {
        if (queryHandler == null) {
        	queryHandler = new QueryHandlerMySQLImpl();
        }
        return queryHandler;
    }
    
    /**
     * Private constructor to disallow instantiation.
     */
    private QueryFactory() {
    	
    }
}
