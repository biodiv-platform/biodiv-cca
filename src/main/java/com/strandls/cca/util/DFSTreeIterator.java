package com.strandls.cca.util;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.strandls.cca.pojo.IChildable;

public class DFSTreeIterator<T extends IChildable<T>> implements Iterator<T> {

	private Deque<T> data;

	public DFSTreeIterator(Collection<T> data) {
		this.data = new LinkedList<>();
		constructDFS(data);
	}

	private void constructDFS(Collection<T> data) {
		if (data == null || data.isEmpty())
			return;

		for (T d : data) {
			this.data.addLast(d);
			constructDFS(d.getChildren());
		}
	}

	@Override
	public boolean hasNext() {
		return !data.isEmpty();
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException("Don't have next element");
		return data.pollFirst();
	}

}
