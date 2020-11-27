package com.alibaba.alink.params.io;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

public interface RandomTableSourceStreamParams<T> extends WithParams <T> {

	/**
	 * Param "idColName"
	 */
	ParamInfo <String> ID_COL = ParamInfoFactory
		.createParamInfo("idCol", String.class)
		.setDescription("id col name")
		.setHasDefaultValue("num")
		.setAlias(new String[] {"idColName"})
		.build();
	/**
	 * Param "outputColConfs"
	 */
	ParamInfo <String> OUTPUT_COL_CONFS = ParamInfoFactory
		.createParamInfo("outputColConfs", String.class)
		.setDescription("output col confs")
		.setHasDefaultValue(null)
		.build();
	/**
	 * Param "outputColNames"
	 */
	ParamInfo <String[]> OUTPUT_COLS = ParamInfoFactory
		.createParamInfo("outputCols", String[].class)
		.setDescription("output col names")
		.setAlias(new String[] {"outputColNames"})
		.setHasDefaultValue(null)
		.build();
	/**
	 * Param "numCols"
	 */
	ParamInfo <Integer> NUM_COLS = ParamInfoFactory
		.createParamInfo("numCols", Integer.class)
		.setDescription("num cols")
		.setRequired()
		.build();
	/**
	 * Param "maxRows"
	 */
	ParamInfo <Long> MAX_ROWS = ParamInfoFactory
		.createParamInfo("maxRows", Long.class)
		.setDescription("max rows")
		.setRequired()
		.build();
	/**
	 * Param "timePerSample"
	 */
	ParamInfo <Double> TIME_PER_SAMPLE = ParamInfoFactory
		.createParamInfo("timePerSample", Double.class)
		.setDescription("time per sample")
		.setHasDefaultValue(null)
		.build();
	/**
	 * Param "timeZones"
	 */
	ParamInfo <Double[]> TIME_ZONES = ParamInfoFactory
		.createParamInfo("timeZones", Double[].class)
		.setDescription("time zones")
		.setHasDefaultValue(null)
		.build();

	default String getIdCol() {return get(ID_COL);}

	default T setIdCol(String value) {return set(ID_COL, value);}

	default String getOutputColConfs() {return get(OUTPUT_COL_CONFS);}

	default T setOutputColConfs(String value) {return set(OUTPUT_COL_CONFS, value);}

	default String[] getOutputCols() {return get(OUTPUT_COLS);}

	default T setOutputCols(String[] value) {return set(OUTPUT_COLS, value);}

	default Integer getNumCols() {return get(NUM_COLS);}

	default T setNumCols(Integer value) {return set(NUM_COLS, value);}

	default Long getMaxRows() {return get(MAX_ROWS);}

	default T setMaxRows(Long value) {return set(MAX_ROWS, value);}

	default Double getTimePerSample() {return get(TIME_PER_SAMPLE);}

	default T setTimePerSample(Double value) {return set(TIME_PER_SAMPLE, value);}

	default Double[] getTimeZones() {return get(TIME_ZONES);}

	default T setTimeZones(Double[] value) {return set(TIME_ZONES, value);}

}
