package pl.art.mnp.rogalin.field.creationdate;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Range implements Serializable {
	public static final Range EMPTY_RANGE = new Range(Integer.MIN_VALUE, Integer.MIN_VALUE);

	private final int from;

	private final int to;

	public Range(int x) {
		this.from = x;
		this.to = x;
	}

	public Range(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public Range add(int x) {
		return new Range(from + x, to + x);
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public boolean contains(Range r) {
		return r.from >= from && r.to <= to;
	}
}
