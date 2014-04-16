package br.com.caelum.monad;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;

public class Transactioned<T> {

	private List<Function<?, ?>> operations;
	private Session session;

	private <R> Transactioned(Session session, Function<R, T> op, List<Function<?, ?>> operations) {
		this.session = session;
		this.operations = new LinkedList<>();
		this.operations.addAll(operations);
		this.operations.add(op);
	}

	public <R> Transactioned<R> map(Function<T, R> op) {
		return new Transactioned<R>(session, op, operations);
	}
	
	public <R> Transactioned<R> flatMap(Function<T, Transactioned<R>> op) {
		throw new UnsupportedOperationException("sei la!");
	}
	
	public static <G> Transactioned<G> begin(Session session, Function<Void, G> op) {
		return new Transactioned<G>(session, op, new LinkedList<Function<?,?>>());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "hiding" })
	public <T> T getOrElse(T fallback) {
		Object value = null;
		session.beginTransaction();
		try {
			for (Function op : operations) {
				value = op.apply(value);
			}
			session.getTransaction().commit();
			return (T) value;
		} catch (Exception e) {
			session.getTransaction().rollback();			
			return fallback;
		}
	}
	
}
