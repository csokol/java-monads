package br.com.caelum.monad;

import java.util.NoSuchElementException;
import java.util.function.Function;

import org.hibernate.Session;

public abstract class Transactioned<T> {

	public abstract <R> Transactioned<R> map(Function<T, R> function);
	
	public abstract <R> Transactioned<R> flatMap(Function<T, Transactioned<R>> function);
	
	public abstract boolean failed();
	
	public abstract Object getOrElse(T i);
	
	public static <G> Transactioned<G> start(Session session, Function<Void, G> function) {
		session.beginTransaction();
		System.out.println("beginning new tx");
		try {
			G result = function.apply(null);
			return new Success<>(result, session);
		} catch (Exception e) {
			session.getTransaction().rollback();
			System.out.println("rollbacking tx!!");
			return new Failure<>(e);
		}
		
	}
	
	public static class Success<T> extends Transactioned<T> {
		private T value;
		private Session session;

		public Success(T value, Session session) {
			this.value = value;
			this.session = session;
		}

		@Override
		public <R> Transactioned<R> map(Function<T, R> function) {
			try {
				R result = function.apply(value);
				System.out.println("happy path");
				return new Success<R>(result, session);
			} catch(Exception e) {
				System.out.println("rollbacking tx!!");
				session.getTransaction().rollback();
				return new Failure<R>(e);
			}
		}

		@Override
		public <R> Transactioned<R> flatMap(Function<T, Transactioned<R>> function) {
			Transactioned<R> tx = function.apply(value);
			return tx;
		}

		@Override
		public boolean failed() {
			return false;
		}

		@Override
		public T getOrElse(T t) {
			return value;
		}
		
	}
	
	public static class Failure<T> extends Transactioned<T> {

		private Exception reason;

		public Failure(Exception reason) {
			this.reason = reason;
		}

		@Override
		public <R> Transactioned<R> map(Function<T, R> function) {
			return new Failure<>(reason);
		}

		@Override
		public <R> Transactioned<R> flatMap(
				Function<T, Transactioned<R>> function) {
			return new Failure<>(reason);
		}

		@Override
		public boolean failed() {
			return true;
		}

		@Override
		public Object getOrElse(T value) {
			return value;
		}
		
	}

}
