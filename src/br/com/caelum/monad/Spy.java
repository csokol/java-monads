package br.com.caelum.monad;

public class Spy {

	private boolean touched;

	public Object touch() {
		this.touched = true;
		return null;
	}

	public boolean touched() {
		return this.touched;
	}

}
