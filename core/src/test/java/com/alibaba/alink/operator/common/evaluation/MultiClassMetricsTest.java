package com.alibaba.alink.operator.common.evaluation;

import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test for MultiClassMetrics.
 */

public class MultiClassMetricsTest extends AlinkTestBase {

	private static final double EPS = 1e-6;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void reduceTest() {
		MultiMetricsSummary metrics1 = new MultiMetricsSummary(
			new long[][] {new long[] {0, 1, 2}, new long[] {3, 1, 0}, new long[] {1, 1, 4}},
			new String[] {"0", "1", "2"}, 0.4, 13);
		MultiMetricsSummary metrics2 = new MultiMetricsSummary(
			new long[][] {new long[] {1, 3, 2}, new long[] {1, 4, 2}, new long[] {2, 0, 2}},
			new String[] {"0", "1", "2"}, 1.4, 17);
		BaseMetricsSummary baseMetricsSummary = metrics1.merge(metrics2);
		Assert.assertTrue(baseMetricsSummary instanceof MultiMetricsSummary);
		MultiMetricsSummary metrics = (MultiMetricsSummary) baseMetricsSummary;

		Assert.assertEquals(30, metrics.total);
		Assert.assertEquals(1.8, metrics.logLoss, 0.01);
		Assert.assertArrayEquals(new String[] {"0", "1", "2"}, metrics.labels);

		long[][] matrix = metrics.matrix.getMatrix();
		Assert.assertArrayEquals(new long[] {1, 4, 4}, matrix[0]);
		Assert.assertArrayEquals(new long[] {4, 5, 2}, matrix[1]);
		Assert.assertArrayEquals(new long[] {3, 1, 6}, matrix[2]);

		baseMetricsSummary = metrics1.merge(null);
		Assert.assertEquals(baseMetricsSummary, metrics1);
	}

	@Test
	public void testReduceDifferentLabelOrder() {
		MultiMetricsSummary metrics1 = new MultiMetricsSummary(
			new long[][] {{0, 1, 2}, {3, 1, 0}, {1, 1, 4}},
			new String[] {"0", "1", "2"}, 0.4, 13);
		MultiMetricsSummary metrics2 = new MultiMetricsSummary(
			new long[][] {{1, 3, 2}, {1, 4, 2}, {2, 0, 2}},
			new String[] {"0", "2", "1"}, 1.4, 17);
		metrics1.merge(metrics2);

		Assert.assertArrayEquals(new Object[] {"2", "1", "0"}, metrics1.labels);
		long[][] matrix = metrics1.matrix.getMatrix();
		Assert.assertArrayEquals(new long[][] {{8, 3, 2}, {0, 3, 5}, {5, 3, 1}}, matrix);
		Assert.assertEquals(1.8, metrics1.logLoss, EPS);
		Assert.assertEquals(30, metrics1.total);
	}

	@Test
	public void testReduceDifferentLabelSet() {
		MultiMetricsSummary metrics1 = new MultiMetricsSummary(
			new long[][] {{1, 0}, {1, 4}},
			new String[] {"1", "2"}, 0.4, 13);
		MultiMetricsSummary metrics2 = new MultiMetricsSummary(
			new long[][] {{1, 3}, {1, 4,}},
			new String[] {"0", "2"}, 1.4, 17);
		metrics1.merge(metrics2);

		Assert.assertArrayEquals(new Object[] {"2", "1", "0"}, metrics1.labels);
		long[][] matrix = metrics1.matrix.getMatrix();
		Assert.assertArrayEquals(new long[][] {{8, 1, 1}, {0, 1, 0}, {3, 0, 1}}, matrix);
		Assert.assertEquals(1.8, metrics1.logLoss, EPS);
		Assert.assertEquals(30, metrics1.total);
	}

	@Test
	public void saveAsParamsTest() {
		MultiMetricsSummary metricsSummary = new MultiMetricsSummary(
			new long[][] {new long[] {0, 3, 1}, new long[] {1, 1, 1}, new long[] {2, 0, 4}},
			new String[] {"0", "1", "2"}, 0.4, 13);

		MultiClassMetrics metrics = metricsSummary.toMetrics();

		Assert.assertEquals(metrics.getMacroRecall(), 0.3055555555555555, 0.01);
		Assert.assertEquals(metrics.getMacroSpecificity(), 0.6973544973544974, 0.01);
		Assert.assertEquals(metrics.getMacroAccuracy(), 0.5897435897435898, 0.01);
		Assert.assertEquals(metrics.getMicroFalseNegativeRate(), 0.6153846153846154, 0.01);
		Assert.assertEquals(metrics.getWeightedRecall(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getWeightedPrecision(), 0.41025641025641024, 0.01);
		Assert.assertEquals(metrics.getMacroPrecision(), 0.3333333333333333, 0.01);
		Assert.assertEquals(metrics.getMicroTruePositiveRate(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getMacroKappa(), 0.01753139066571909, 0.01);
		Assert.assertEquals(metrics.getMicroSpecificity(), 0.6923076923076923, 0.01);
		Assert.assertEquals(metrics.getMacroF1(), 0.31746031746031744, 0.01);
		Assert.assertEquals(metrics.getWeightedKappa(), 0.10234541577825165, 0.01);
		Assert.assertEquals(metrics.getWeightedTruePositiveRate(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getMicroTrueNegativeRate(), 0.6923076923076923, 0.01);
		Assert.assertEquals(metrics.getMicroSensitivity(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getWeightedAccuracy(), 0.6153846153846154, 0.01);
		Assert.assertEquals(metrics.getAccuracy(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getWeightedFalseNegativeRate(), 0.6153846153846154, 0.01);
		Assert.assertEquals(metrics.getMicroF1(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getWeightedSpecificity(), 0.7074481074481075, 0.01);
		Assert.assertEquals(metrics.getWeightedF1(), 0.39560439560439564, 0.01);
		Assert.assertEquals(metrics.getMicroAccuracy(), 0.5897435897435898, 0.01);
		Assert.assertEquals(metrics.getWeightedTrueNegativeRate(), 0.7074481074481075, 0.01);
		Assert.assertEquals(metrics.getKappa(), 0.04587155963302759, 0.01);
		Assert.assertEquals(metrics.getMacroSensitivity(), 0.3055555555555555, 0.01);
		Assert.assertEquals(metrics.getWeightedSensitivity(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getMicroRecall(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getMacroFalseNegativeRate(), 0.6944444444444445, 0.01);
		Assert.assertEquals(metrics.getLogLoss(), 0.030, 0.01);
		Assert.assertEquals(metrics.getMicroFalsePositiveRate(), 0.3076923076923077, 0.01);
		Assert.assertEquals(metrics.getWeightedFalsePositiveRate(), 0.29255189255189257, 0.01);
		Assert.assertEquals(metrics.getMicroPrecision(), 0.38461538461538464, 0.01);
		Assert.assertEquals(metrics.getMacroTrueNegativeRate(), 0.6973544973544974, 0.01);
		Assert.assertEquals(metrics.getMicroKappa(), 0.0769230769230769, 0.01);

		Assert.assertArrayEquals(metrics.getActualLabelFrequency(), new long[] {3, 4, 6});
		Assert.assertEquals(metrics.getActualLabelProportion().length, 3);
		Assert.assertArrayEquals(metrics.getPredictLabelFrequency(), new long[] {4, 3, 6});
		Assert.assertEquals(metrics.getPredictLabelProportion().length, 3);
		Assert.assertEquals(metrics.getTruePositiveRate("0"), 0.0, 0.01);
		Assert.assertEquals(metrics.getTrueNegativeRate("0"), 0.6, 0.01);
		Assert.assertEquals(metrics.getFalsePositiveRate("0"), 0.4, 0.01);
		Assert.assertEquals(metrics.getFalseNegativeRate("0"), 1.0, 0.01);
		Assert.assertEquals(metrics.getMacroFalsePositiveRate(), 0.30, 0.01);
		Assert.assertEquals(metrics.getSpecificity("0"), 0.6, 0.01);
		Assert.assertEquals(metrics.getSensitivity("0"), 0.0, 0.01);
		Assert.assertEquals(metrics.getRecall("0"), 0.0, 0.01);
		Assert.assertEquals(metrics.getPrecision("0"), 0.0, 0.01);
		Assert.assertEquals(metrics.getAccuracy("0"), 0.46, 0.01);
		Assert.assertEquals(metrics.getF1("0"), 0.0, 0.01);
		Assert.assertEquals(metrics.getKappa("0"), -0.35, 0.01);

		System.out.println(metrics.toString());
	}
}