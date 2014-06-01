package com.nm.var.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.nm.var.src.VarUtils;

public class VarUtils_UnitTest
{
    private final double delta = 0.0001;

    @Test
    public void shouldReturnCorrectNumberOfReturnsFromCSVFile()
    {
        assertTrue( 2 == VarUtils.getReturnsFromFile( new File( "testing/testingFile.csv" ) ).length );
    }

    @Test
    public void shouldReturnCorrectNumberOfReturnsFromMSFTFile()
    {
        assertTrue( 67 == VarUtils.getReturnsFromFile( new File(
                                                                 "testing/MSFT_15082013_15112013.csv" ) ).length );
    }

    @Test
    public void shouldReturnCorrectSquareOfNumber()
    {
        double sent = 1;
        double received = VarUtils.square( sent );
        assertTrue( received == sent );
        sent = 2;
        received = VarUtils.square( sent );
        assertTrue( received == sent * sent );
        sent = 20;
        received = VarUtils.square( sent );
        assertTrue( received == sent * sent );
    }

    @Test
    public void shouldReturnCorrectSquareRootOfNumber()
    {
        double sent = 1;
        double received = VarUtils.root( sent );
        assertTrue( received == sent );
        sent = 4;
        received = VarUtils.root( sent );
        assertTrue( received == 2 );
        sent = 625;
        received = VarUtils.root( sent );
        assertTrue( received == 25 );
    }

    @Test
    public void shouldReturnCorrectRoundedNumber()
    {
        double sent = 1.03;
        double received = VarUtils.round( sent );
        assertTrue( received == 1 );
        sent = 2.51;
        received = VarUtils.round( sent );
        assertTrue( received == 3.0 );
        sent = 20.496;
        received = VarUtils.round( sent );
        assertTrue( received == 20 );
    }

    @Test
    public void shouldComputeCorrectVolatilityUsingStandardFormula()
    {
        double[] returnsFromFile = VarUtils.getReturnsFromFile( new File(
                                                                          "testing/GOOG_190913_181013.csv" ) );
        double computedVolatility = VarUtils.computeVolatility_Standard( returnsFromFile );
        double expectedVolatility = 0.0298958;
        System.out.println( "Standard volatility: " + computedVolatility );
        assertEquals( expectedVolatility, computedVolatility, delta );
    }

    @Test
    public void shouldComputeCorrectVolatilityUsingEWMA()
    {
        double[] returnsFromFile = VarUtils.getReturnsFromFile( new File(
                                                                          "testing/GOOG_190913_181013.csv" ) );
        double computedVolatility = VarUtils.computeVolatility_EWMA( returnsFromFile );
        double expectedVolatility = 0.06011;
        System.out.println( "EWMA volatility: " + computedVolatility );
        assertEquals( expectedVolatility, computedVolatility, delta );
    }

    @Test
    public void shouldComputeCorrectVolatilityUsingGARCH()
    {
        double[] returnsFromFile = VarUtils.getReturnsFromFile( new File(
                                                                          "testing/GOOG_190913_181013.csv" ) );
        double computedVolatility = VarUtils.computeVolatility_GARCH( returnsFromFile );
        System.out.println( "GARCH volatility: " + computedVolatility );
    }

    /**
     * <a
     * href="http://www.investopedia.com/articles/financial-theory/11/calculating-covariance.asp">
     * Source</a>
     */
    @Test
    public void shouldReturnExpectedCovarianceForReturns()
    {
        double[] returns1 = { 1.1, 1.7, 2.1, 1.4, 0.2 };
        double[] returns2 = { 3, 4.2, 4.9, 4.1, 2.5 };
        double covariance = VarUtils.getCovariance( returns1, returns2 );
        assertTrue( covariance == 0.665 );
    }

    /**
     * Two stock example test from book.
     */
    @Test
    public void shouldComputeSimilarCovarianceUsingEWMA()
    {
    }

    @Test
    public void shouldComputeSimilarCovarianceUsingGarch()
    {
    }

    @Test
    public void shouldReturnCorrectCovarianceUsingManualCalculation()
    {
        double[] returns = { 1, 2, 3 };
        double covariance = VarUtils.getCovarianceManually( returns, returns );
        assertEquals( covariance, VarUtils.getCovariance( returns, returns ), 0.01 );

        double[] returns1 = { 1, 2, 3, 2, 1 };
        double[] returns2 = { 1, 2, 3, 2, 1, 2 };
        covariance = VarUtils.getCovarianceManually( returns1, returns2 );
        assertEquals( covariance, VarUtils.getCovariance( returns1, returns2 ),
                      0.01 );

        double[] oppReturns = { 3, 2, 1, 2, 3 };
        covariance = VarUtils.getCovarianceManually( returns, oppReturns );
        assertEquals( covariance, VarUtils.getCovariance( returns, oppReturns ),
                      0.01 );
    }

    @Test
    @Ignore
    public void shouldDecomposeMatrix()
    {
        // values from online page
        // http://www.sitmo.com/article/generating-correlated-random-numbers/

        double[][] inputMatrix = { { 1, 0.6, 0.3 }, { 0.6, 1, 0.5 },
                { 0.3, 0.5, 1 } };
        double[][] decomposedMatrix = VarUtils.decomposeMatrix( inputMatrix );
        double[][] expectedDecomposedMatrix = { { 1, 0.0, 0.0 },
                { 0.6, 0.8, 0.0 }, { 0.3, 0.4, 0.866 } };

        Arrays.deepEquals( decomposedMatrix, expectedDecomposedMatrix );

        // tested by hand.
    }

    @Test
    public void shouldGetCorrectPercentileFromSeries()
    {
        double[] data = new double[100];
        for( int i = 0 ; i < 100 ; i++ )
        {
            data[i] = i + 1;
        }
        int percentile = 10;
        double received = VarUtils.getPercentile( data, percentile );
        assertEquals( received, 90.0, 1.0 );
    }

    @Test
    @Ignore
    public void shouldGetCorrectReturnsOverHorizon()
    {
        double[] returns = { 1.0, 2.0, 3.0, 4.0, 5.0 };
        @SuppressWarnings("unused")
        double[] returnsOverVarHorizon = VarUtils.getReturnsOverVarHorizon( returns, 2 );
        // tested by hand
    }

}
