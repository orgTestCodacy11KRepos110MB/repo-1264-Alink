package com.alibaba.alink.operator.common.optim.subfunc;

import org.apache.flink.api.java.tuple.Tuple2;

import com.alibaba.alink.common.comqueue.ComContext;
import com.alibaba.alink.common.comqueue.ComputeFunction;
import com.alibaba.alink.common.linalg.DenseVector;

import java.util.List;

/**
 * Preallocate memory of dense vector, which will be used by optimizer.
 */
public class PreallocateVector extends ComputeFunction {
	private static final long serialVersionUID = 8639853924159859121L;
	private final String name;
	private double[] value = new double[] {0.0};

	/**
	 * construct function
	 *
	 * @param name name for this memory.
	 */
	public PreallocateVector(String name) {
		this.name = name;
	}

	/**
	 * construct function
	 *
	 * @param name  name for this memory.
	 * @param value the double values allocated with vector.
	 */
	public PreallocateVector(String name, double[] value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void calc(ComContext context) {
		if (context.getStepNo() == 1) {
			List <DenseVector> coefs = context.getObj(OptimVariable.model);
			DenseVector coef = coefs.get(0);
			DenseVector vec = new DenseVector(coef.size());
			context.putObj(name, Tuple2.of(vec, value));
		}
	}
}
