package br.com.caelum.monad;

import org.hibernate.Session;
import org.junit.Test;

public class TransactionedTest {

	@Test
	public void test() {
		Object result = Transactioned.start(new Session(), (x) -> 10)
			.map(ten -> (int) ten * 2)
			.map(value -> bugous())
			.map(value -> (int) value * 2)
			.map(value -> (int) value * 2)
			.getOrElse(666);
		
		System.out.println(result);
	}

	private Object bugous() {
		throw new RuntimeException();
	}

}
