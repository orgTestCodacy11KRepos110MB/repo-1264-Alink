package com.alibaba.alink.params.dataproc;

import com.alibaba.alink.params.shared.HasNumThreads;
import com.alibaba.alink.params.shared.colname.HasOutputColDefaultAsNull;
import com.alibaba.alink.params.shared.colname.HasReservedColsDefaultAsNull;
import com.alibaba.alink.params.shared.colname.HasSelectedCol;

/**
 * Parameters for {@link com.alibaba.alink.pipeline.dataproc.StringIndexerModel}.
 */
public interface StringIndexerPredictParams<T> extends
	HasSelectedCol <T>,
	HasReservedColsDefaultAsNull <T>,
	HasHandleInvalid <T>,
	HasOutputColDefaultAsNull <T>, HasNumThreads <T> {
}
