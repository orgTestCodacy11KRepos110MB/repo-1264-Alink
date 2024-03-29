package com.alibaba.alink.operator.common.linear.unarylossfunc;

import com.alibaba.alink.common.exceptions.AkIllegalArgumentException;

/**
 * SVR (Support Vector Regression) loss function.
 */
public class SvrLossFunc implements UnaryLossFunc {

	private static final long serialVersionUID = 2882192117339178786L;
	private final double epsilon;

	public SvrLossFunc(double epsilon) {
		if (epsilon < 0) {
			throw new AkIllegalArgumentException("Parameter epsilon can not be negtive.");
		}
		this.epsilon = epsilon;
	}

	@Override
	public double loss(double eta, double y) {
		return Math.max(0, Math.abs(eta - y) - epsilon);
	}

	@Override
	public double derivative(double eta, double y) {
		if (Math.abs(eta - y) > epsilon) {
			return Math.signum(eta - y);
		} else {
			return 0;
		}
	}

	@Override
	public double secondDerivative(double eta, double y) {
		return 0;
	}

}
