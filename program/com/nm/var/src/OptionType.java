package com.nm.var.src;

/**
 * Enumeration of the possible option types usable in the program.
 *
 */
public enum OptionType
{
    AMERICAN_PUT( 0 ),
    AMERICAN_CALL( 1 ),
    EUROPEAN_PUT( 2 ),
    EUROPEAN_CALL( 3 );

    /** Integer equivalent of the option type. */
    private int value;

    /**
     * Create an option type with an integer equivalent for inference.
     * @param value
     */
    private OptionType( int value )
    {
        this.value = value;
    }
    /**
     * 
     * @return integer equivalent of the option type.
     */
    public int getValue()
    {
        return value;
    }
}
