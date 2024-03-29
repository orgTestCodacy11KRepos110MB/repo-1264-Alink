package com.alibaba.alink.common;

import com.alibaba.alink.common.exceptions.AkIllegalArgumentException;

import java.util.HashMap;

/**
 * Factory to get the MLEnvironment using a MLEnvironmentId.
 *
 * <p>The following code snippet shows how to interact with MLEnvironmentFactory.
 * <pre>
 * {@code
 * long mlEnvId = MLEnvironmentFactory.getNewMLEnvironmentId();
 * MLEnvironment mlEnv = MLEnvironmentFactory.get(mlEnvId);
 * }
 * </pre>
 */
public class MLEnvironmentFactory {

	/**
	 * The default MLEnvironmentId.
	 */
	public static final Long DEFAULT_ML_ENVIRONMENT_ID = 0L;

	/**
	 * A monotonically increasing id for the MLEnvironments.
	 * Each id uniquely identifies an MLEnvironment.
	 */
	private static Long nextId = 1L;

	/**
	 * Map that hold the MLEnvironment and use the MLEnvironmentId as its key.
	 */
	private static final HashMap <Long, MLEnvironment> map = new HashMap <>();

	/**
	 * Get the MLEnvironment using a MLEnvironmentId.
	 * The default MLEnvironment will be set a new MLEnvironment
	 * when there is no default MLEnvironment.
	 *
	 * @param mlEnvId the MLEnvironmentId
	 * @return the MLEnvironment
	 */
	public static synchronized MLEnvironment get(Long mlEnvId) {
		if (!map.containsKey(mlEnvId)) {
			if (mlEnvId.equals(DEFAULT_ML_ENVIRONMENT_ID)) {
				setDefault(new MLEnvironment());
			} else {
				throw new AkIllegalArgumentException(
					String.format("Cannot find MLEnvironment for MLEnvironmentId %s." +
						" Did you get the MLEnvironmentId by calling getNewMLEnvironmentId?", mlEnvId));
			}
		}

		return map.get(mlEnvId);
	}

	/**
	 * Get the MLEnvironment use the default MLEnvironmentId.
	 *
	 * @return the default MLEnvironment.
	 */
	public static synchronized MLEnvironment getDefault() {
		return get(DEFAULT_ML_ENVIRONMENT_ID);
	}

	/**
	 * Set the default MLEnvironment.
	 * The default MLEnvironment should be set only once.
	 *
	 * @param env the MLEnvironment
	 */
	public static synchronized void setDefault(MLEnvironment env) {
		if (map.containsKey(DEFAULT_ML_ENVIRONMENT_ID)) {
			throw new AkIllegalArgumentException("The default MLEnvironment should be set only once.");
		}

		map.put(DEFAULT_ML_ENVIRONMENT_ID, env);
	}

	/**
	 * Create a unique MLEnvironment id and register a new MLEnvironment in the factory.
	 *
	 * @return the MLEnvironment id.
	 */
	public static synchronized Long getNewMLEnvironmentId() {
		return registerMLEnvironment(new MLEnvironment());
	}

	/**
	 * Register a new MLEnvironment to the factory and return a new MLEnvironment id.
	 *
	 * @param env the MLEnvironment that will be stored in the factory.
	 * @return the MLEnvironment id.
	 */
	public static synchronized Long registerMLEnvironment(MLEnvironment env) {
		map.put(nextId, env);
		return nextId++;
	}

	/**
	 * Remove the MLEnvironment using the MLEnvironmentId.
	 *
	 * @param mlEnvId the id.
	 * @return the removed MLEnvironment
	 */
	public static synchronized MLEnvironment remove(Long mlEnvId) {
		return map.remove(mlEnvId);
	}
}
