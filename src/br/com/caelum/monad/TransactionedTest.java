package br.com.caelum.monad;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class TransactionedTest {

	@Test
	public void rollback() {
		Object result = Transactioned.begin(new Session(), (x) -> 10)
			.map(ten -> (int) ten * 2)
			.map(value -> bugous())
			.map(value -> (int) value * 2)
			.map(value -> (int) value * 2)
			.getOrElse(666);
		
		Assert.assertEquals(666, result);
	}
	
	@Test
	public void commit() {
		Object forty = Transactioned.begin(new Session(), (x) -> 10)
				.map(ten -> (int) ten * 2)
				.map(value -> (int) value * 2)
				.getOrElse(666);
		
		Assert.assertEquals(40, forty);
		
	}
	
	@Test
	public void shouldBeLazy() {
		final Spy spy = new Spy();
		Transactioned<Object> tx = Transactioned.begin(new Session(), (x) -> 10)
				.map(ten -> (int) ten * 2)
				.map(value -> spy.touch());
		
		Assert.assertFalse(spy.touched());
		tx.getOrElse(1);
		Assert.assertTrue(spy.touched());
	}

	private Object bugous() {
		throw new RuntimeException();
	}

}
