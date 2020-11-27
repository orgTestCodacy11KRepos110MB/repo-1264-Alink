package com.alibaba.alink.operator.batch.sql;

import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.operator.batch.BatchOperator;

/**
 * Remove duplicated records.
 */
public final class DistinctBatchOp extends BaseSqlApiBatchOp <DistinctBatchOp> {

	private static final long serialVersionUID = 2774293287356122519L;

	public DistinctBatchOp() {
		this(new Params());
	}

	public DistinctBatchOp(Params params) {
		super(params);
	}

	@Override
	public DistinctBatchOp linkFrom(BatchOperator <?>... inputs) {
		this.setOutputTable(inputs[0].distinct().getOutputTable());
		return this;
	}
}
