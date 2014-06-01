package com.nm.var.src;

/**
 * Class implementing the Binomial Tree option pricing algorithm.
 * Can be initialised with an {@link Option} or separate values.
 */
public class BinomialTree
{
    private double S, X, T, volatility, interest;
    private int    optionType;
    /** The time step. One day is desirable. */
    // private int n = 1;
    private int      numberOfSteps;
    private double   dt;
    private double   p;
    private double   optionPrice;

    /**
     * Constructor to initialise a binomial tree using parameters of an option
     * @param s initial stock price
     * @param x strike price
     * @param t in years, time to maturity
     * @param volatility of the underlying asset
     * @param interest rate of option
     * @param type of option
     */
    public BinomialTree( double s, double x, double t, double volatility, double interest, int type )
    {
        S = s;
        X = x;
        T = t;
        this.volatility = volatility;
        this.interest = interest;
        this.optionType = type;
        buildStockPriceTree();
    }

    /**
     * Constructor to initialise a binomial tree using an option.
     * Infers parameters from the option.
     * @param option the option to price
     */
    public BinomialTree( Option option )
    {
        this.S = option.getInitialStockPrice();
        this.X = option.getStrike();
        this.T = option.getTimeToMaturity(); // in days
        this.volatility = option.getDailyVolatility();
        this.interest = option.getInterest();
        this.optionType = option.getOptionType();
        buildStockPriceTree();
    }
    
    /**
     * Builds the binomial tree with stock prices going up and down.
     */
    public void buildStockPriceTree()
    {
        double u, d;

        // p is probability of up move, 1-p probability of down move

        // convert T to days
        // double days = T * DAYS_IN_YEAR;
        // int monthlySteps = (int) days / DAYS_IN_MONTH;

        // dailySteps = days / day in year
        int numberOfDays = (int) Math.floor( T * 252 );

        // dt = T / steps
        dt = T / numberOfDays;

        // http://en.wikipedia.org/wiki/Binomial_options_pricing_model

        u = Math.exp( ( volatility * Math.sqrt( dt ) ) );
        d = 1 / u;

        // u >= 1, 0 < d <= 1
        if( ( u < 1 ) || ( d < 0 ) || ( d > 1 ) )
        {
            System.out.println( "Error calculating u and d." );
            return;
        }

        // p = (e^(rdt) - d) / (u-d) (between 0 and 1) if dt < (variance)/(r-q)^2
        double ert = Math.exp( interest * dt );
        double ertminusd = ert - d;
        double uminusd = u - d;
        p = ertminusd / uminusd;

        if( p < 0 || p > 1 )
        {
            System.out.println( "Error: p = " + p );
        }

        // numberOfSteps = (int) ( T * 12 ) + 1;
        numberOfSteps = numberOfDays + 1;

        double[][] tree = new double[numberOfSteps][numberOfSteps];
        tree[0][0] = S;

        for( int row = 0 ; row < numberOfSteps ; row++ )
        {
            int startDay = row;
            for( int day = startDay ; day < numberOfSteps ; day++ )
            {
                if( row == 0 && day == 0 )
                {
                    continue;
                }
                else if( row == day )
                {
                    tree[row][day] = tree[row - 1][day - 1] * d;
                    continue;
                }
                // up
                tree[row][day] = tree[row][day - 1] * u;
                // down
            }
        }

        //printTree( tree );

        calculateOptionPrices( tree );

    }
    
    /**
     * Calculates the option prices using a tree of stock prices.
     * @param tree model of stock prices going up and down in a binomial tree
     */
    private void calculateOptionPrices( double[][] tree )
    {
        double[][] values = new double[tree.length][tree.length];

        // compute values at final nodes
        // at each final node (expiration of Option) option value is intrinsic/exercise value

        // for Call - Max [(Sn - X), 0] Sn = price of stock on that day
        // for Put - Max [(X - Sn), 0]
        for( int row = 0 ; row < numberOfSteps ; row++ )
        {
            int column = numberOfSteps - 1;

            switch( optionType )
            {
                case VarUtils.AMERICAN_CALL:
                    values[row][column] = Math.max( ( tree[row][column] - X ), 0 );
                    break;
                case VarUtils.AMERICAN_PUT:
                    values[row][column] = Math.max( ( X - tree[row][column] ), 0 );
                    break;
                case VarUtils.EUROPEAN_CALL:
                    values[row][column] = Math.max( ( tree[row][column] - X ), 0 );
                    break;
                case VarUtils.EUROPEAN_PUT:
                    values[row][column] = Math.max( ( X - tree[row][column] ), 0 );
                    break;
            }
        }
        System.out.println( "-----------------------------------" );
        printTree( values );

        double eMinusRT = Math.exp( -1.0 * interest * dt );

        for( int column = numberOfSteps - 2 ; column >= 0 ; column-- )
        {
            for( int row = 0 ; row <= column ; row++ )
            {
                double binomialValue = 0.0;
                double intrinsicValue = 0.0;
                switch( optionType )
                {
                    case VarUtils.AMERICAN_CALL:
                        binomialValue = eMinusRT
                                        * ( ( p * values[row][column + 1] ) + ( 1 - p )
                                                                              * values[row + 1][column + 1] );
                        // intrinsic value for call is Max(Sn - X, 0)
                        intrinsicValue = Math.max( tree[row][column] - X, 0 );
                        values[row][column] = Math.max( binomialValue, intrinsicValue );
                        break;
                    case VarUtils.AMERICAN_PUT:
                        // early exercise possible, value = Max [Binomial Value, Exercise Value]
                        binomialValue = eMinusRT
                                        * ( ( p * values[row][column + 1] ) + ( 1 - p )
                                                                              * values[row + 1][column + 1] );
                        intrinsicValue = Math.max( X - tree[row][column], 0 );
                        // intrinsic value for put is Max(X-Sn, 0)
                        values[row][column] = Math.max( binomialValue, intrinsicValue );
                        break;
                    case VarUtils.EUROPEAN_CALL:
                        // no option of early exercise, so binomial value applies
                        binomialValue = eMinusRT
                                        * ( ( p * values[row][column + 1] ) + ( 1 - p )
                                                                              * values[row + 1][column + 1] );
                        values[row][column] = binomialValue;
                        break;
                    case VarUtils.EUROPEAN_PUT:
                        binomialValue = eMinusRT
                                        * ( ( p * values[row][column + 1] ) + ( 1 - p )
                                                                              * values[row + 1][column + 1] );
                        values[row][column] = binomialValue;
                        break;
                }
            }
        }
        System.out.println( "--------------------------------" );
        printTree( values );
        optionPrice = values[0][0];
    }
    /**
     * Prints the tree as a 2D table for visualisation of computation.
     * @param tree
     */
    private void printTree( double[][] tree )
    {
        for( int row = 0 ; row < tree[0].length ; row++ )
        {
            for( int column = 0 ; column < tree.length ; column++ )
            {
                System.out.print( VarUtils.roundTwoDP( tree[row][column] ) + "\t" );
            }
            System.out.println();
        }
    }

    // TODO control variate technique
    /*
     * 1. value American option use tree to get fA
     * 2. value corresponding European option using same tree to get fE
     * 3. value European option using Black-Scholes to get fBS
     * 4. take fA + fBS - fE as estimate of price of American option
     * 5. (fBS - fE) can be thought of as the error of the tree method,
     * assume error for American option is the same.
     */
    
    /**
     * @return the final option price computed using the binomial tree method.
     */
    public double getOptionPrice()
    {
        return optionPrice;
    }

}
