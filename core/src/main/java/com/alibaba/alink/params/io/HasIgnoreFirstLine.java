package com.alibaba.alink.params.io;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;

public interface HasIgnoreFirstLine<T> extends WithParams <T> {

	@NameCn("是否忽略第一行数据")
	@DescCn("是否忽略第一行数据")
	ParamInfo <Boolean> IGNORE_FIRST_LINE = ParamInfoFactory
		.createParamInfo("ignoreFirstLine", Boolean.class)
		.setDescription("Whether to ignore first line of csv file.")
		.setHasDefaultValue(false)
		.build();

	default Boolean getIgnoreFirstLine() {
		return get(IGNORE_FIRST_LINE);
	}

	default T setIgnoreFirstLine(Boolean value) {
		return set(IGNORE_FIRST_LINE, value);
	}
}
