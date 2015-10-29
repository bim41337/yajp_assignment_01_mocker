package de.oth.mocker;

public class VerificationInformation {
	
	private VerificationType type;
	private Integer count;
	
	public VerificationInformation(VerificationType type, Integer count) {
		this.type = type;
		this.count = count;
	}

	VerificationType getType() {
		return type;
	}

	Integer getCount() {
		return count;
	}

}
