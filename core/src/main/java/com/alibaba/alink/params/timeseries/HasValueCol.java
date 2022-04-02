package com.alibaba.alink.params.timeseries;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;

public interface HasValueCol<T> extends WithParams <T> {

	@NameCn("value列，类型为MTable")
	@DescCn("value列，类型为MTable")
	ParamInfo <String> VALUE_COL = ParamInfoFactory
		.createParamInfo("valueCol", String.class)
		.setDescription("The value col.")
		.setAlias(new String[] {"dataCol", "dataColName"})
		.setRequired()
		.build();

	default String getValueCol() {
		return get(VALUE_COL);
	}

	default T setValueCol(String value) {
		return set(VALUE_COL, value);
	}
}
