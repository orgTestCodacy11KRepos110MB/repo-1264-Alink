package com.alibaba.alink.operator.batch.similarity;

import org.apache.flink.ml.api.misc.param.Params;
import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.params.shared.HasNumThreads;
import com.alibaba.alink.params.similarity.StringTextApproxNearestNeighborTrainParams;
import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.alink.operator.batch.similarity.StringNearestNeighborBatchOpTest.extractScore;

/**
 * Test for ApproxStringSimilarityTopNBatchOp.
 */
public class TextApproxNearestNeighborBatchOpTest extends AlinkTestBase {

	public static Row[] dictRows = new Row[] {
		Row.of("dict1", "A B C E"),
		Row.of("dict2", "A C M"),
		Row.of("dict3", "A B"),
		Row.of("dict4", "D E A N"),
		Row.of("dict5", "D"),
		Row.of("dict6", "A F E C B A")
	};
	public static Row[] queryRows = new Row[] {
		Row.of(1, "A B C"),
		Row.of(2, "A C B"),
		Row.of(3, "B B A"),
		Row.of(4, "D E A"),
		Row.of(5, "E E C"),
		Row.of(6, "A E D")
	};

	@Test
	public void testSimHashSim() throws Exception {
		BatchOperator dict = new MemSourceBatchOp(Arrays.asList(dictRows), new String[] {"id", "str"});
		BatchOperator query = new MemSourceBatchOp(Arrays.asList(queryRows), new String[] {"id", "str"});

		TextApproxNearestNeighborTrainBatchOp train = new TextApproxNearestNeighborTrainBatchOp()
			.setIdCol("id")
			.setSelectedCol("str")
			.setMetric(StringTextApproxNearestNeighborTrainParams.Metric.SIMHASH_HAMMING_SIM)
			.linkFrom(dict);

		TextApproxNearestNeighborPredictBatchOp predict = new TextApproxNearestNeighborPredictBatchOp(
			new Params().set(HasNumThreads.NUM_THREADS, 4))
			.setSelectedCol("str")
			.setTopN(3)
			.setOutputCol("topN")
			.linkFrom(train, query);

		List <Row> res = predict.collect();

		Map <Object, Double[]> score = new HashMap <>();
		score.put(1, new Double[] {0.984375, 0.953125, 0.9375});
		score.put(2, new Double[] {0.984375, 0.953125, 0.9375});
		score.put(3, new Double[] {0.921875, 0.875, 0.875});
		score.put(4, new Double[] {0.9375, 0.890625, 0.8125});
		score.put(5, new Double[] {0.890625, 0.84375, 0.8125});
		score.put(6, new Double[] {0.9375, 0.890625, 0.8125});

		for (Row row : res) {
			Double[] actual = extractScore((String) row.getField(2));
			Double[] expect = score.get(row.getField(0));
			for (int i = 0; i < actual.length; i++) {
				Assert.assertEquals(actual[i], expect[i], 0.01);
			}
		}
	}

	@Test
	public void testSimHash() throws Exception {
		BatchOperator dict = new MemSourceBatchOp(Arrays.asList(dictRows), new String[] {"id", "str"});
		BatchOperator query = new MemSourceBatchOp(Arrays.asList(queryRows), new String[] {"id", "str"});

		TextApproxNearestNeighborTrainBatchOp train = new TextApproxNearestNeighborTrainBatchOp()
			.setIdCol("id")
			.setSelectedCol("str")
			.setMetric(StringTextApproxNearestNeighborTrainParams.Metric.SIMHASH_HAMMING)
			.linkFrom(dict);

		TextApproxNearestNeighborPredictBatchOp predict = new TextApproxNearestNeighborPredictBatchOp()
			.setSelectedCol("str")
			.setTopN(3)
			.setOutputCol("topN")
			.linkFrom(train, query);

		List <Row> res = predict.collect();

		Map <Object, Double[]> score = new HashMap <>();
		score.put(1, new Double[] {1.0, 3.0, 4.0});
		score.put(2, new Double[] {1.0, 3.0, 4.0});
		score.put(3, new Double[] {5.0, 8.0, 8.0});
		score.put(4, new Double[] {4.0, 7.0, 12.0});
		score.put(5, new Double[] {7.0, 10.0, 12.0});
		score.put(6, new Double[] {4.0, 7.0, 12.0});

		for (Row row : res) {
			Double[] actual = extractScore((String) row.getField(2));
			Double[] expect = score.get(row.getField(0));
			for (int i = 0; i < actual.length; i++) {
				Assert.assertEquals(actual[i], expect[i], 0.01);
			}
		}
	}

	@Test
	public void testMinHash() throws Exception {
		BatchOperator dict = new MemSourceBatchOp(Arrays.asList(dictRows), new String[] {"id", "str"});
		BatchOperator query = new MemSourceBatchOp(Arrays.asList(queryRows), new String[] {"id", "str"});

		TextApproxNearestNeighborTrainBatchOp train = new TextApproxNearestNeighborTrainBatchOp()
			.setIdCol("id")
			.setSelectedCol("str")
			.setMetric(StringTextApproxNearestNeighborTrainParams.Metric.MINHASH_JACCARD_SIM)
			.linkFrom(dict);

		TextApproxNearestNeighborPredictBatchOp predict = new TextApproxNearestNeighborPredictBatchOp()
			.setSelectedCol("str")
			.setTopN(3)
			.setOutputCol("topN")
			.linkFrom(train, query);

		List <Row> res = predict.collect();

		Map <Object, Double[]> score = new HashMap <>();
		score.put(1, new Double[] {0.7, 0.6, 0.6});
		score.put(2, new Double[] {0.7, 0.6, 0.6});
		score.put(3, new Double[] {1.0, 0.5, 0.4});
		score.put(4, new Double[] {0.7, 0.4, 0.3});
		score.put(5, new Double[] {0.5, 0.4, 0.3});
		score.put(6, new Double[] {0.7, 0.4, 0.3});

		for (Row row : res) {
			Double[] actual = extractScore((String) row.getField(2));
			Double[] expect = score.get(row.getField(0));
			for (int i = 0; i < actual.length; i++) {
				Assert.assertEquals(actual[i], expect[i], 0.01);
			}
		}
	}

	@Test
	public void testJaccard() throws Exception {
		BatchOperator dict = new MemSourceBatchOp(Arrays.asList(dictRows), new String[] {"id", "str"});
		BatchOperator query = new MemSourceBatchOp(Arrays.asList(queryRows), new String[] {"id", "str"});

		TextApproxNearestNeighborTrainBatchOp train = new TextApproxNearestNeighborTrainBatchOp()
			.setIdCol("id")
			.setSelectedCol("str")
			.setMetric(StringTextApproxNearestNeighborTrainParams.Metric.JACCARD_SIM)
			.linkFrom(dict);

		TextApproxNearestNeighborPredictBatchOp predict = new TextApproxNearestNeighborPredictBatchOp()
			.setSelectedCol("str")
			.setTopN(3)
			.setOutputCol("topN")
			.linkFrom(train, query);

		List <Row> res = predict.collect();

		Map <Object, Double[]> score = new HashMap <>();
		score.put(1, new Double[] {0.75, 0.666, 0.5});
		score.put(2, new Double[] {0.75, 0.666, 0.5});
		score.put(3, new Double[] {0.666, 0.4, 0.285});
		score.put(4, new Double[] {0.75, 0.4, 0.333});
		score.put(5, new Double[] {0.4, 0.285, 0.2});
		score.put(6, new Double[] {0.75, 0.4, 0.333});

		for (Row row : res) {
			Double[] actual = extractScore((String) row.getField(2));
			Double[] expect = score.get(row.getField(0));
			for (int i = 0; i < actual.length; i++) {
				Assert.assertEquals(actual[i], expect[i], 0.01);
			}
		}
	}

}