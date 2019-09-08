package net.aionstudios.livecomply.aos.ajax;

import java.util.Map;

import org.json.JSONException;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.util.DatabaseUtils;
import net.aionstudios.livecomply.aos.util.AjaxUtils;

public class AjaxClockThirtyAction extends Action {

	public AjaxClockThirtyAction() {
		super("clockThirty");
		this.setPostRequiredParams("ajax_token","sessionID","eid","time");
	}

	private String addTenQuery = "INSERT INTO `livecomply`.`clock_events` (`eid`, `clock_event_type`, `clock_event_hour24`, `clock_event_minute60`) VALUES (?, ?, ?, ?);";
	
	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery) throws JSONException {
		if(AjaxUtils.isValidAjaxToken(postQuery)) {
			int eid = Integer.parseInt(postQuery.get("eid").replaceFirst("^0+(?!$)", ""));
			int ct = Integer.parseInt(postQuery.get("time"));
			int hour24 = (ct/100)%24;
			int min60 = (ct%100)%60;
			DatabaseUtils.prepareAndExecute(addTenQuery, true, eid, "THIRTY", hour24, min60);
			response.putData("added", true);
			response.putDataResponse(ResponseStatus.SUCCESS, "done");
			return;
		}
		response.putErrorResponse(InternalErrors.unauthorizedAccessError, "The Ajax service couldn't not validate your access.");
	}
	
}