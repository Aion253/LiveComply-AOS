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

public class AjaxClockInAction extends Action {

	public AjaxClockInAction() {
		super("clockIn");
		this.setPostRequiredParams("ajax_token","sessionID","eid","ctime","name");
	}

	private String eidPullQuery = "SELECT `minor`,`name` FROM `livecomply`.`employees` WHERE `eid` = ?;";
	private String createEidQuery = "INSERT INTO `livecomply`.`employees` (`eid`, `name`) VALUES (?, ?);";
	private String clockedIn = "SELECT COUNT(*) FROM `livecomply`.`clock_events` WHERE `eid` = ?;";
	private String clockIn = "INSERT INTO `livecomply`.`clock_events` (`eid`,`clock_event_type`,`clock_event_hour24`,`clock_event_minute60`) VALUES (?,?,?,?);";
	
	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery) throws JSONException {
		if(AjaxUtils.isValidAjaxToken(postQuery)) {
			int eid = Integer.parseInt(postQuery.get("eid").replaceFirst("^0+(?!$)", ""));
			List<Map<String,Object>> res = DatabaseUtils.prepareAndExecute(eidPullQuery, true, postQuery.get("eid").replaceFirst("^0+(?!$)", "")).get(0).getResults();
			if(res.isEmpty()) {
				DatabaseUtils.prepareAndExecute(createEidQuery, true, eid, postQuery.get("name"));
			}
			if(!((long)DatabaseUtils.prepareAndExecute(clockedIn, true, eid).get(0).getResults().get(0).get("COUNT(*)")==0)) {
				response.putErrorResponse(LiveComplyAOS.invalidClockEventError, "Employee already clocked in.");
				return;
			}
			int cin = Integer.parseInt(postQuery.get("ctime"));
			int hour24 = (cin/100)%24;
			int min60 = (cin%100)%60;
			DatabaseUtils.prepareAndExecute(clockIn, true, eid, "IN", hour24, min60);
			response.putData("clocked_in", true);
			response.putDataResponse(ResponseStatus.SUCCESS, "done");
			return;
		}
		response.putErrorResponse(InternalErrors.unauthorizedAccessError, "The Ajax service couldn't not validate your access.");
	}

}
