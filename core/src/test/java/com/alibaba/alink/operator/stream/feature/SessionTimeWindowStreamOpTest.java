package com.alibaba.alink.operator.stream.feature;

import org.apache.flink.types.Row;

import com.alibaba.alink.operator.stream.StreamOperator;
import com.alibaba.alink.operator.stream.source.MemSourceStreamOp;
import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class SessionTimeWindowStreamOpTest extends AlinkTestBase {
	@Test
	public void test() throws Exception {
		List <Row> sourceFrame = Arrays.asList(
			Row.of(0, 0, 0, new Timestamp(1000L)),
			Row.of(0, 2, 0, new Timestamp(2000L)),
			Row.of(0, 1, 1, new Timestamp(3000L)),
			Row.of(0, 3, 1, new Timestamp(4000L)),
			Row.of(0, 3, 3, new Timestamp(5000L)),
			Row.of(0, 0, 3, new Timestamp(7000L)),
			Row.of(0, 0, 4, new Timestamp(8000L)),
			Row.of(0, 3, 4, new Timestamp(9000L)),
			Row.of(0, 1, 2, new Timestamp(10000L))
		);
		StreamOperator <?> source = new MemSourceStreamOp(
			sourceFrame, new String[] {"user", "device", "ip", "ts"});

		source.link(
			new SessionTimeWindowStreamOp()
				.setTimeCol("ts")
				.setSessionGapTime("2s")
				.setGroupCols("user")
				.setClause("SESSION_START() as start_time, SESSION_END() as end_time, count_preceding(ip) as count_ip")
		).print();

		StreamOperator.execute();
	}

	@Test
	public void test2() throws Exception {
		List <Row> sourceFrame = Arrays.asList(
			Row.of(0, 0, 0, new Timestamp(1000L)),
			Row.of(0, 2, 0, new Timestamp(2000L)),
			Row.of(0, 1, 1, new Timestamp(3000L)),
			Row.of(0, 3, 1, new Timestamp(4000L)),
			Row.of(0, 3, 3, new Timestamp(5000L)),
			Row.of(0, 0, 3, new Timestamp(7000L)),
			Row.of(0, 0, 4, new Timestamp(8000L)),
			Row.of(0, 3, 4, new Timestamp(9000L)),
			Row.of(0, 1, 2, new Timestamp(10000L))
		);
		StreamOperator <?> source = new MemSourceStreamOp(
			sourceFrame, new String[] {"user", "device", "ip", "ts"});

		source.link(
			new SessionTimeWindowStreamOp()
				.setTimeCol("ts")
				.setSessionGapTime(2)
				.setGroupCols("user")
				.setClause("SESSION_START() as start_time, SESSION_END() as end_time, count_preceding(ip) as count_ip")
		).print();

		StreamOperator.execute();
	}
}