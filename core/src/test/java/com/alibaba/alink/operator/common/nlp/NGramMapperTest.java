package com.alibaba.alink.operator.common.nlp;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.ml.api.misc.param.Params;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;

import com.alibaba.alink.common.exceptions.AkIllegalOperatorParameterException;
import com.alibaba.alink.params.nlp.NGramParams;
import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for NGramMapper.
 */

public class NGramMapperTest extends AlinkTestBase {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testDefault() throws Exception {
		TableSchema schema = new TableSchema(new String[] {"sentence"}, new TypeInformation <?>[] {Types.STRING});

		Params params = new Params()
			.set(NGramParams.SELECTED_COL, "sentence");

		NGramMapper mapper = new NGramMapper(schema, params);

		assertEquals(mapper.map(Row.of("This is a unit test for mapper")).getField(0),
			"This_is is_a a_unit unit_test test_for for_mapper");
		assertEquals(mapper.getOutputSchema(), schema);
	}

	@Test
	public void testN() throws Exception {
		TableSchema schema = new TableSchema(new String[] {"sentence", "id"},
			new TypeInformation <?>[] {Types.STRING, Types.INT});

		Params params = new Params()
			.set(NGramParams.SELECTED_COL, "sentence")
			.set(NGramParams.N, 3);

		NGramMapper mapper = new NGramMapper(schema, params);

		assertEquals(mapper.map(Row.of("This is a unit test for mapper", 1)).getField(0),
			"This_is_a is_a_unit a_unit_test unit_test_for test_for_mapper");
		assertEquals(mapper.map(Row.of(null, 2)).getField(0), null);
		assertEquals(mapper.getOutputSchema(), schema);
	}

	@Test
	public void testExceptionN() throws Exception {
		TableSchema schema = new TableSchema(new String[] {"sentence"}, new TypeInformation <?>[] {Types.STRING});

		Params params = new Params()
			.set(NGramParams.SELECTED_COL, "sentence")
			.set(NGramParams.N, 10);

		NGramMapper mapper = new NGramMapper(schema, params);

		assertEquals(mapper.map(Row.of("This is a unit test for mapper")).getField(0), "");
		assertEquals(mapper.getOutputSchema(), schema);
	}

	@Test
	public void testException() throws Exception {
		TableSchema schema = new TableSchema(new String[] {"sentence"}, new TypeInformation <?>[] {Types.STRING});

		Params params = new Params()
			.set(NGramParams.SELECTED_COL, "sentence")
			.set(NGramParams.N, -1);

		thrown.expect(AkIllegalOperatorParameterException.class);
		NGramMapper mapper = new NGramMapper(schema, params);
	}
}
