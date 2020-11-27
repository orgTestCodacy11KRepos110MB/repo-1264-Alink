package com.alibaba.alink.params.recommendation;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;

import com.alibaba.alink.params.shared.colname.HasGroupCol;
import com.alibaba.alink.params.shared.colname.HasOutputCol;

public interface Zipped2KObjectParams<T> extends
	HasGroupCol <T>,
	HasObjectCol <T>,
	HasOutputCol <T> {

	ParamInfo <String[]> INFO_COLS = ParamInfoFactory
		.createParamInfo("infoCols", String[].class)
		.setDescription("Names of the columns used for external information")
		.build();

	default String[] getInfoCols() {
		return get(INFO_COLS);
	}

	default T setInfoCols(String... colNames) {
		return set(INFO_COLS, colNames);
	}
}
