package com.alibaba.alink.params.udf;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

/**
 * @author dota.zk
 * @date 25/03/2021
 */
public interface HasPythonVersion<T> extends WithParams<T> {
    ParamInfo<String> PYTHON_VERSION = ParamInfoFactory
        .createParamInfo("pythonVersion", String.class)
        .setDescription("the version of python, example 3.6, 3.7, 3.8")
        .setOptional()
        .setHasDefaultValue("3.7")
        .build();


    default T sePythonVersion(String version) {
        return set(PYTHON_VERSION, version);
    }

    default String getPythonVersion() {
        return get(PYTHON_VERSION);
    }
}
