package net.aionstudios.livecomply.aos.ajax;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.util.DatabaseUtils;
import net.aionstudios.livecomply.aos.LiveComplyAOS;
import net.aionstudios.livecomply.aos.util.AjaxUtils;

public class AjaxClockOutAction extends Action {

	public AjaxClockOutAction() {
		super("clockOut");
		this.setPostRequiredParams("ajax_token","sessionID","eid");
	}

	private String deleteEventsQuery = "DELETE FROM `livecomply`.`clock_events` WHERE `eid` = ?;";
	
	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery) throws JSONException {
		if(AjaxUtils.isValidAjaxToken(postQuery)) {
			int eid = Integer.parseInt(postQuery.get("eid").replaceFirst("^0+(?!$)", ""));
			DatabaseUtils.prepareAndExecute(deleteEventsQuery, true, eid);
			response.putData("clocked_in", false);
			response.putDataResponse(ResponseStatus.SUCCESS, "done");
			return;
		}
		response.putErrorResponse(InternalErrors.unauthorizedAccessError, "The Ajax service couldn't not validate your access.");
	}

}