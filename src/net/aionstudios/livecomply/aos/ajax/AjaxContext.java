package net.aionstudios.livecomply.aos.ajax;

import org.json.JSONException;

import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.context.Context;
import net.aionstudios.api.response.Response;

public class AjaxContext extends Context {

	public AjaxContext() {
		super("ajax");
	}

	@Override
	public void contextDefault(Response response, String requestContext) throws JSONException {
		response.putDataResponse(ResponseStatus.SUCCESS, "You've reached the asynchronous application portal for Live Comply. Requests to this system will require validation.");
	}

}
