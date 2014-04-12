package org.hibernate;

import br.com.caelum.monad.TransactionedTest;

public class Session {

	public void beginTransaction() {
	}

	public Transaction getTransaction() {
		return new Transaction();
	}

}
