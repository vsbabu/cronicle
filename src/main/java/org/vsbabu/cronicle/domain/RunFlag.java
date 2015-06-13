package org.vsbabu.cronicle.domain;

public enum RunFlag {

	RAN_TOO_LONG(1);
	
	private int value;
	
	RunFlag(int value) {
		this.value = value;
	}
}
