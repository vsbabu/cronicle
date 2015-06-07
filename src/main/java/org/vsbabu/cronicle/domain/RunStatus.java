package org.vsbabu.cronicle.domain;

public enum RunStatus {
	
	UNSCHEDULED(0), SCHEDULED(1), RUNNING(2), SUCCESS(3), FAILED(4), DID_NOT_RUN(5), RAN_TOO_LONG(6);
	
	private int value;
	
	RunStatus(int value) {
		this.value = value;
	}
	
	public boolean isTerminated() {
		return (this.value > 2);
	}
	
	public boolean isWip() {
		return (this.value == 2);
	}

	public boolean isPending() {
		return (this.value <= 2);
	}


}
