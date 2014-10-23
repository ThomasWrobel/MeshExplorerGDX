package com.lostagain.nl.uti;

public interface SpiffyGenericTween<T extends Number> {
	public T next();
	public T previous();
	public boolean hasNext();
	public T endPoint();
	
	
}
