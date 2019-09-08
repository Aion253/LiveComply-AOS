package net.aionstudios.livecomply.aos.errors;

import net.aionstudios.api.error.AOSError;

public class InvalidClockEventError extends AOSError {

	public InvalidClockEventError() {
		super("InvalidClockEventError", 200, "The request attempted to insert a clock event that conflicts with a current memory state.");
	}

}
