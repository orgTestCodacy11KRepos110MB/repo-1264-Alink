package com.alibaba.alink.params.shared.colname;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;

/**
 * Trait for parameter vectorColName.
 */
public interface HasVectorColDefaultAsNull<T> extends WithParams <T> {

	@NameCn("向量列名")
	@DescCn("向量列对应的列名，默认值是null")
	ParamInfo <String> VECTOR_COL = ParamInfoFactory
		.createParamInfo("vectorCol", String.class)
		.setDescription("Name of a vector column")
		.setHasDefaultValue(null)
		.setAlias(new String[] {"vectorColName", "tensorColName", "vecColName"})
		.build();

	default String getVectorCol() {
		return get(VECTOR_COL);
	}

	default T setVectorCol(String colName) {
		return set(VECTOR_COL, colName);
	}
}
