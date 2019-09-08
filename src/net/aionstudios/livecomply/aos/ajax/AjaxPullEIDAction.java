package net.aionstudios.livecomply.aos.ajax;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.util.DatabaseUtils;
import net.aionstudios.livecomply.aos.util.AjaxUtils;

public class AjaxPullEIDAction extends Action {

	public AjaxPullEIDAction() {
		super("pullEid");
		this.setPostRequiredParams("ajax_token","sessionID","eid");
	}

	private String eidPullQuery = "SELECT `minor`,`name` FROM `livecomply`.`employees` WHERE `eid` = ?;";
	
	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery) throws JSONException {
		if(AjaxUtils.isValidAjaxToken(postQuery)) {
			List<Map<String,Object>> res = DatabaseUtils.prepareAndExecute(eidPullQuery, true, Integer.parseInt(postQuery.get("eid").replaceFirst("^0+(?!$)", ""))).get(0).getResults();
			if(!res.isEmpty()) {
				response.putData("has_eid", true);
				response.putData("name", (String) res.get(0).get("name"));
				response.putData("minor", (int) res.get(0).get("minor") == 0 ? false : true);
				response.putDataResponse(ResponseStatus.SUCCESS, "done");
				return;
			}
			response.putData("has_eid", false);
			response.putDataResponse(ResponseStatus.SUCCESS, "done");
			return;
		}
		response.putErrorResponse(InternalErrors.unauthorizedAccessError, "The Ajax service couldn't not validate your access.");
	}

}
