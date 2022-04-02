package com.alibaba.alink.operator.batch.dataproc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.utils.DataSetUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.ml.api.misc.param.Params;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import com.alibaba.alink.common.annotation.InputPorts;
import com.alibaba.alink.common.annotation.NameCn;
import com.alibaba.alink.common.annotation.OutputPorts;
import com.alibaba.alink.common.annotation.PortDesc;
import com.alibaba.alink.common.annotation.PortSpec;
import com.alibaba.alink.common.annotation.PortType;
import com.alibaba.alink.common.annotation.SelectedColsWithFirstInputSpec;
import com.alibaba.alink.common.utils.OutputColsHelper;
import com.alibaba.alink.common.utils.TableUtil;
import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.params.dataproc.HugeMultiStringIndexerPredictParams;

import java.util.Arrays;
import java.util.List;

import static com.alibaba.alink.params.dataproc.HasHandleInvalid.HandleInvalid.ERROR;
import static com.alibaba.alink.params.dataproc.HasHandleInvalid.HandleInvalid.SKIP;

/**
 * Map string to index based on the model generated by {@link MultiStringIndexerTrainBatchOp}.
 */
@InputPorts(values = {
	@PortSpec(value = PortType.MODEL, desc = PortDesc.PREDICT_INPUT_MODEL),
	@PortSpec(value = PortType.DATA, desc = PortDesc.PREDICT_INPUT_DATA)
})
@OutputPorts(values = {@PortSpec(value = PortType.DATA, desc = PortDesc.OUTPUT_RESULT)})
@SelectedColsWithFirstInputSpec
@NameCn("并行ID化预测")
public final class HugeStringIndexerPredictBatchOp
	extends BatchOperator <HugeStringIndexerPredictBatchOp>
	implements HugeMultiStringIndexerPredictParams<HugeStringIndexerPredictBatchOp> {

	private static final long serialVersionUID = -8879911265448892835L;

	public HugeStringIndexerPredictBatchOp() {
		this(new Params());
	}

	public HugeStringIndexerPredictBatchOp(Params params) {
		super(params);
	}

	/**
	 * Extract the token to index mapping from the model. The <code>selectedCols</code> should be a subset
	 * of those columns used to train the model.
	 *
	 * @param model        The model fitted by {@link StringIndexerTrainBatchOp}.
	 * @return A DataSet of tuples of column index, token, token index.
	 */
	private DataSet <Tuple2 <String, Long>> getModelData(BatchOperator model) {
		DataSet <Row> modelRows = model.getDataSet();
		return modelRows
			.flatMap(new FlatMapFunction <Row, Tuple2 <String, Long>>() {
				private static final long serialVersionUID = 923102381848510928L;

				@Override
				public void flatMap(Row row, Collector <Tuple2 <String, Long>> out) throws Exception {
						out.collect(Tuple2.of((String) row.getField(0), (Long) row.getField(1)));
				}
			})
			.name("get_model_data")
			.returns(new TupleTypeInfo <>(Types.STRING, Types.LONG));
	}

	@Override
	public HugeStringIndexerPredictBatchOp linkFrom(BatchOperator <?>... inputs) {
		Params params = super.getParams();
		BatchOperator model = inputs[0];
		BatchOperator data = inputs[1];

		String[] selectedColNames = params.get(HugeMultiStringIndexerPredictParams.SELECTED_COLS);
		String[] outputColNames = params.get(HugeMultiStringIndexerPredictParams.OUTPUT_COLS);
		if (outputColNames == null) {
			outputColNames = selectedColNames;
		}
		String[] keepColNames = params.get(HugeMultiStringIndexerPredictParams.RESERVED_COLS);
		TypeInformation[] outputColTypes = new TypeInformation[outputColNames.length];
		Arrays.fill(outputColTypes, Types.LONG);

		OutputColsHelper outputColsHelper = new OutputColsHelper(data.getSchema(), outputColNames,
			outputColTypes, keepColNames);

		final int[] selectedColIdx = TableUtil.findColIndicesWithAssertAndHint(data.getSchema(), selectedColNames);
		final HandleInvalid handleInvalidStrategy
			= HandleInvalid
			.valueOf(params.get(HugeMultiStringIndexerPredictParams.HANDLE_INVALID).toString());

		DataSet <Tuple2 <Long, Row>> dataWithId = DataSetUtils.zipWithUniqueId(data.getDataSet());

		DataSet <Tuple2 <String, Long>> modelData = getModelData(model);

		// tuple: column index, default token id
		DataSet <Long> defaultIndex = modelData.mapPartition(new MapPartitionFunction <Tuple2 <String, Long>, Long>() {
				@Override
				public void mapPartition(Iterable <Tuple2 <String, Long>> iterable, Collector <Long> collector) throws Exception {
					Long max = 0L;
					for(Tuple2<String, Long> value : iterable) {
						max = Math.max(max, value.f1);
					}
					collector.collect(max);
				}
			})
			.reduceGroup(new GroupReduceFunction <Long, Long>() {
				@Override
				public void reduce(Iterable <Long> iterable, Collector <Long> collector) throws Exception {
					Long max = 0L;
					for(Long value : iterable) {
						max = Math.max(max, value);
					}
					collector.collect(max + 1L);
				}
			})
			.name("get_default_index")
			.returns(Types.LONG);

		// tuple: record id, column index, token
		DataSet <Tuple3 <Long, Integer, String>> flattened = dataWithId
			.flatMap(new RichFlatMapFunction <Tuple2 <Long, Row>, Tuple3 <Long, Integer, String>>() {
				private static final long serialVersionUID = 1917562146323592635L;

				@Override
				public void flatMap(Tuple2 <Long, Row> value, Collector <Tuple3 <Long, Integer, String>> out)
					throws Exception {
					for (int i = 0; i < selectedColIdx.length; i++) {
						Object o = value.f1.getField(selectedColIdx[i]);
						if (o != null) {
							out.collect(Tuple3.of(value.f0, i, String.valueOf(o)));
						}
					}
				}
			})
			.name("flatten_pred_data")
			.returns(new TupleTypeInfo <>(Types.LONG, Types.INT, Types.STRING));

		// tuple: record id, column index, token id
		DataSet <Tuple3 <Long, Integer, Long>> indexedNulTokens = dataWithId
			.flatMap(new FlatMapFunction <Tuple2 <Long, Row>, Tuple3 <Long, Integer, Long>>() {
				private static final long serialVersionUID = 2893742534366079246L;

				@Override
				public void flatMap(Tuple2 <Long, Row> value, Collector <Tuple3 <Long, Integer, Long>> out)
					throws Exception {
					for (int i = 0; i < selectedColIdx.length; i++) {
						Object o = value.f1.getField(selectedColIdx[i]);
						if (o == null) {
							// because null value is ignored during training, so it will always
							// be treated as "unseen" token.
							out.collect(Tuple3.of(value.f0, i, -1L));
						}
					}
				}
			})
			.name("map_null_token_to_index")
			.returns(new TupleTypeInfo <>(Types.LONG, Types.INT, Types.LONG));

		// record id, column index, token index
		DataSet <Tuple3 <Long, Integer, Long>> indexed = flattened
			.leftOuterJoin(modelData)
			.where(2).equalTo(0)
			.with(
				new JoinFunction <Tuple3 <Long, Integer, String>, Tuple2 <String, Long>, Tuple3 <Long, Integer, Long>>() {
					private static final long serialVersionUID = 2270459281179536013L;

					@Override
					public Tuple3 <Long, Integer, Long> join(Tuple3 <Long, Integer, String> first,
															 Tuple2 <String, Long> second) throws Exception {
						if (second == null) {
							return Tuple3.of(first.f0, first.f1, -1L);
						} else {
							return Tuple3.of(first.f0, first.f1, second.f1);
						}
					}
				})
			.name("map_token_to_index")
			.returns(new TupleTypeInfo <>(Types.LONG, Types.INT, Types.LONG));

		// tuple: record id, prediction result
		DataSet <Tuple2 <Long, Row>> aggregateResult = indexed
			.union(indexedNulTokens)
			.groupBy(0)
			.reduceGroup(new RichGroupReduceFunction <Tuple3 <Long, Integer, Long>, Tuple2 <Long, Row>>() {
				private static final long serialVersionUID = -1581264399340055162L;
				//transient long[] defaultIndex;
				transient Long defaultIndex;

				@Override
				public void open(Configuration parameters) throws Exception {
					if (handleInvalidStrategy.equals(SKIP)
						|| handleInvalidStrategy.equals(ERROR)) {
						return;
					}
					List <Long> bc = getRuntimeContext().getBroadcastVariable("defaultIndex");
					defaultIndex = bc.get(0);
				}

				@Override
				public void reduce(Iterable <Tuple3 <Long, Integer, Long>> values, Collector <Tuple2 <Long, Row>> out)
					throws Exception {

					Long id = null;
					Row r = new Row(selectedColIdx.length);
					for (Tuple3 <Long, Integer, Long> v : values) {
						Long index = v.f2;
						if (index == -1L) {
							switch (handleInvalidStrategy) {
								case KEEP:
									index = defaultIndex;
									index = index == null ? 0L : index;
									break;
								case SKIP:
									index = null;
									break;
								case ERROR:
									throw new RuntimeException("Unknown token.");
							}
						}

						int col = v.f1;
						r.setField(col, index);
						id = v.f0;
					}
					out.collect(Tuple2.of(id, r));
				}
			})
			.withBroadcastSet(defaultIndex, "defaultIndex")
			.name("aggregate_result")
			.returns(new TupleTypeInfo <>(Types.LONG, new RowTypeInfo(outputColTypes)));

		DataSet <Row> output = dataWithId
			.join(aggregateResult)
			.where(0).equalTo(0)
			.with(new JoinFunction <Tuple2 <Long, Row>, Tuple2 <Long, Row>, Row>() {
				private static final long serialVersionUID = 3724539437313089427L;

				@Override
				public Row join(Tuple2 <Long, Row> first, Tuple2 <Long, Row> second) throws Exception {
					return outputColsHelper.getResultRow(first.f1, second.f1);
				}
			})
			.name("merge_result")
			.returns(new RowTypeInfo(outputColsHelper.getResultSchema().getFieldTypes()));

		this.setOutput(output, outputColsHelper.getResultSchema());
		return this;
	}
}
