package com.nm.var.src;

/**
 * A class to implement the Black-Scholes formula for calculating option prices.
 * 
 * @author Nishant
 * 
 */
public class BlackScholes
{   
    /** Identifier of a call option. */
    public static final int CALL = 0;
    /** Identifier of a put option. */
    public static final int PUT  = 1;
    
    /** Empty constructor. */
    public BlackScholes()
    {
    }

    /**
     * Computes the price of a European Call/Put option based on the parameters specified.
     * 
     * @param flag
     *            0 for call, 1 for put
     * @param S
     *            Starting price of the option
     * @param X
     *            Strike price of the option
     * @param T
     *            Time left to maturity
     * @param r
     *            Risk-free interest rate
     * @param v
     *            Volatility of the option
     * @return
     */
    public double compute( int flag, double S, double X, double T, double r,
                           double v )
    {
        double d1, d2;

        d1 = ( Math.log( S / X ) + ( r + v * v / 2 ) * T ) / ( v * Math.sqrt( T ) );
        d2 = d1 - v * Math.sqrt( T );

        if( flag == CALL )
        {
            return S * VarUtils.CNDF( d1 ) - X * Math.exp( -r * T ) * VarUtils.CNDF( d2 );
        }
        else if( flag == PUT )
        {
            return X * Math.exp( -r * T ) * VarUtils.CNDF( -d2 ) - S * VarUtils.CNDF( -d1 );
        }
        else
            return -1.0;
    }
}
