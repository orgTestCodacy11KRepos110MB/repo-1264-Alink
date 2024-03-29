package com.alibaba.alink.operator.common.distance;

import org.apache.flink.ml.api.misc.param.Params;
import org.apache.flink.types.Row;

import com.alibaba.alink.common.exceptions.AkIllegalDataException;
import com.alibaba.alink.common.exceptions.AkPreconditions;
import com.alibaba.alink.common.linalg.DenseMatrix;

/**
 * Save the data for calculating distance fast. The FastDistanceMatrixData saves several dense vectors in a single
 * matrix.
 * The vectors are organized in columns, which means each column is a single vector. For example, vec1: 0,1,2, vec2:
 * 3,4,5, vec3: 6,7,8, then the data in
 * matrix is organized as: vec1,vec2,vec3. And the data array in <code>vectors</code> is {0,1,2,3,4,5,6,7,8}.
 */
public class FastDistanceMatrixData extends FastDistanceData {
	private static final long serialVersionUID = 3093977891649431843L;
	/**
	 * Stores several dense vectors in columns. For example, if the vectorSize is n, and matrix saves m vectors, then
	 * the
	 * number of rows of <code>vectors</code> is n and the number of cols of <code>vectors</code> is m.
	 */
	final DenseMatrix vectors;

	/**
	 * Stores some extra info extracted from the vector. It's also organized in columns. For example, if we want to
	 * save
	 * the L1 norm and L2 norm of the vector, then the two values are viewed as a two-dimension label vector. We
	 * organize
	 * the norm vectors together to get the <code>label</code>. If the number of cols of <code>vectors</code> is m,
	 * then in this case the dimension of <code>label</code> is 2 * m.
	 */
	DenseMatrix label;

	/**
	 * Constructor, initialize the vector data.
	 *
	 * @param vectors DenseMatrix which saves vectors in columns.
	 */
	public FastDistanceMatrixData(DenseMatrix vectors) {
		this(vectors, null);
	}

	/**
	 * Constructor, initialize the vector data and extra info.
	 *
	 * @param vectors DenseMatrix which saves vectors in columns.
	 * @param rows    extra info besides the vector.
	 */
	public FastDistanceMatrixData(DenseMatrix vectors, Row[] rows) {
		super(rows);
		AkPreconditions.checkNotNull(vectors, "DenseMatrix should not be null!");
		if (null != rows) {
			AkPreconditions.checkArgument(vectors.numCols() == rows.length,
				new AkIllegalDataException("The column number of DenseMatrix must be equal to the rows array length!"));
		}
		this.vectors = vectors;
	}

	/**
	 * @param matrixData
	 */
	public FastDistanceMatrixData(FastDistanceMatrixData matrixData) {
		super(matrixData);
		this.vectors = matrixData.vectors.clone();
		this.label = (null == matrixData.label) ? null : matrixData.label.clone();
	}

	public DenseMatrix getVectors() {
		return vectors;
	}

	public DenseMatrix getLabel() {
		return label;
	}

	@Override
	public String toString() {
		Params params = new Params();
		params.set("vectors", vectors);
		params.set("label", label);
		params.set("rows", rows);
		return params.toJson();
	}

	public static FastDistanceMatrixData fromString(String s) {
		Params params = Params.fromJson(s);
		Row[] row = params.get("rows", Row[].class);
		DenseMatrix vectors = params.get("vectors", DenseMatrix.class);
		FastDistanceMatrixData matrixData = new FastDistanceMatrixData(vectors, row);
		matrixData.label = params.get("label", DenseMatrix.class);
		return matrixData;
	}
}
