package com.alibaba.alink.pipeline.dataproc.vector;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;

import com.alibaba.alink.common.linalg.DenseVector;
import com.alibaba.alink.common.linalg.Vector;
import com.alibaba.alink.common.linalg.VectorUtil;
import com.alibaba.alink.operator.AlgoOperator;
import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.operator.common.dataproc.SortUtils.RowComparator;
import com.alibaba.alink.operator.stream.StreamOperator;
import com.alibaba.alink.operator.stream.sink.CollectSinkStreamOp;
import com.alibaba.alink.operator.stream.source.MemSourceStreamOp;
import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VectorPolynomialExpandTest extends AlinkTestBase {

	AlgoOperator getData(boolean isBatch) {
		TableSchema schema = new TableSchema(
			new String[] {"id", "c0", "c1", "c2"},
			new TypeInformation <?>[] {Types.STRING, Types.STRING, Types.STRING, Types.STRING}
		);

		List <Row> rows = new ArrayList <>();

		rows.add(Row.of(new Object[] {"0", "$6$1:2.0 2:3.0 5:4.3", "3.0 2.0 3.0", "1 4 6 8"}));
		rows.add(Row.of(new Object[] {"1", "$8$1:2.0 2:3.0 7:4.3", "3.0 2.0 3.0", "1 4 6 8"}));
		rows.add(Row.of(new Object[] {"2", "$8$1:2.0 2:3.0 7:4.3", "2.0 3.0", "1 4 6 8"}));

		if (isBatch) {
			return new MemSourceBatchOp(rows, schema);
		} else {
			return new MemSourceStreamOp(rows, schema);
		}
	}

	@Test
	public void pipelineBatchTest() throws Exception {
		BatchOperator res = new VectorPolynomialExpand()
			.setDegree(2)
			.setOutputCol("outv")
			.setSelectedCol("c1").transform((BatchOperator) getData(true));
		List rows = res.getDataSet().collect();
		HashMap <String, Vector> map = new HashMap <String, Vector>();
		map.put((String) ((Row) rows.get(0)).getField(0), VectorUtil.getVector(((Row) rows.get(0)).getField(4)));
		map.put((String) ((Row) rows.get(1)).getField(0), VectorUtil.getVector(((Row) rows.get(1)).getField(4)));
		map.put((String) ((Row) rows.get(2)).getField(0), VectorUtil.getVector(((Row) rows.get(2)).getField(4)));
		assertEquals(map.get("2"),
			new DenseVector(new double[] {2.0, 4.0, 3.0, 6.0, 9.0}));
		assertEquals(map.get("1"),
			new DenseVector(new double[] {3.0, 9.0, 2.0, 6.0, 4.0, 3.0, 9.0, 6.0, 9.0}));
		assertEquals(map.get("0"),
			new DenseVector(new double[] {3.0, 9.0, 2.0, 6.0, 4.0, 3.0, 9.0, 6.0, 9.0}));
	}

	@Test
	public void pipelineStreamTest() throws Exception {
		StreamOperator streamOperator =
			new VectorPolynomialExpand()
				.setDegree(2)
				.setOutputCol("outv")
				.setSelectedCol("c1").transform((StreamOperator) getData(false));
		CollectSinkStreamOp collectSinkStreamOp = new CollectSinkStreamOp().linkFrom(streamOperator);
		StreamOperator.execute();
		List <Row> result = collectSinkStreamOp.getAndRemoveValues();
		result.sort(new RowComparator(0));
		assertEquals(VectorUtil.getVector(result.get(2).getField(4)),
			new DenseVector(new double[] {2.0, 4.0, 3.0, 6.0, 9.0}));
		assertEquals(VectorUtil.getVector(result.get(1).getField(4)),
			new DenseVector(new double[] {3.0, 9.0, 2.0, 6.0, 4.0, 3.0, 9.0, 6.0, 9.0}));
		assertEquals(VectorUtil.getVector(result.get(0).getField(4)),
			new DenseVector(new double[] {3.0, 9.0, 2.0, 6.0, 4.0, 3.0, 9.0, 6.0, 9.0}));
	}
}