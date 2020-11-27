package com.alibaba.alink.operator.batch.nlp;

import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TfidfBatchOpTest extends AlinkTestBase {
	@Test
	public void test() throws Exception {
		//docment array
		Row[] array2 = new Row[] {
			Row.of("doc0", "中国", 1L),
			Row.of("doc0", "的", 1L),
			Row.of("doc0", "文化", 1L),
			Row.of("doc1", "只要", 1L),
			Row.of("doc1", "中国", 2L),
			Row.of("doc1", "功夫", 1L),
			Row.of("doc1", "深", 1L),
			Row.of("doc2", "北京", 1L),
			Row.of("doc2", "的", 1L),
			Row.of("doc2", "拆迁", 1L),
			Row.of("doc3", "人名", 1L),
			Row.of("doc3", "的", 1L),
			Row.of("doc3", "名义", 1L)
		};
		TfidfBatchOp tfidf = new TfidfBatchOp()
			.setDocIdCol("docid")
			.setWordCol("word")
			.setCountCol("cnt");

		//Generate MemSourceBatchOp via array2
		MemSourceBatchOp data = new MemSourceBatchOp(Arrays.asList(array2), new String[] {"docid", "word", "cnt"});

		//Print tf idf result
		Assert.assertEquals(tfidf.linkFrom(
			data
		).count(), 13);

		tfidf.print();
	}

}