/**
 * 
 */
package com.nm.var.src;

/**
 * An enumeration of all the currently allowed values of confidence level.
 */
public enum ConfidenceLevel
{
    NINETYNINE( 99 ),
    NINETYFIVE( 95 ),
    NINETY( 90 ),
    EIGHTYFIVE( 85 ),
    EIGHTY( 80 ),
    SEVENTYFIVE( 75 );

    private int value;

    ConfidenceLevel( int value )
    {
        this.value = value;
    }
    
    /**
     * @return the integer value of a confidence level enumerated variable.
     */
    public int getValue()
    {
        return value;
    }
    
    /**
     * @return an array of strings to populate the drop-down menu models with.
     */
    public static String[] getStringValues()
    {
        String[] values = { "99%", "95%", "90%", "85%", "80%", "75%" };

        return values;
    }
}
