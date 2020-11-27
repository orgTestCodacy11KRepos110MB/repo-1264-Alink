package com.alibaba.alink.operator.stream.feature;

import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.common.feature.QuantileDiscretizerModelMapper;
import com.alibaba.alink.operator.stream.utils.ModelMapStreamOp;
import com.alibaba.alink.params.feature.QuantileDiscretizerPredictParams;

/**
 * The stream operator that predict the data using the quantile discretizer model.
 */
public class QuantileDiscretizerPredictStreamOp extends ModelMapStreamOp <QuantileDiscretizerPredictStreamOp>
	implements QuantileDiscretizerPredictParams <QuantileDiscretizerPredictStreamOp> {

	private static final long serialVersionUID = 6454721782371885502L;

	public QuantileDiscretizerPredictStreamOp(BatchOperator model) {
		this(model, null);
	}

	public QuantileDiscretizerPredictStreamOp(BatchOperator model, Params params) {
		super(model, QuantileDiscretizerModelMapper::new, params);
	}
}
