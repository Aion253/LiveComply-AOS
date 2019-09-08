package net.aionstudios.livecomply.aos.ajax;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.service.ResponseServices;
import net.aionstudios.api.util.DatabaseUtils;
import net.aionstudios.livecomply.aos.LiveComplyAOS;
import net.aionstudios.livecomply.aos.clocks.ActiveClockBreaks;
import net.aionstudios.livecomply.aos.util.AjaxUtils;

public class AjaxUpdateBreaksAction  extends Action {

	public AjaxUpdateBreaksAction() {
		super("updateBreaks");
		this.setPostRequiredParams("ajax_token","sessionID");
	}

	private String selectAllClockEvents = "SELECT * FROM `livecomply`.`clock_events`;";
	private String selectAllEmployees = "SELECT * FROM `livecomply`.`employees`;";
	private Map<Integer, ActiveClockBreaks> breakers;
	
	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery) throws JSONException {
		if(AjaxUtils.isValidAjaxToken(postQuery)) {
			breakers = new HashMap<Integer, ActiveClockBreaks>();
			for(Map<String,Object> m : DatabaseUtils.prepareAndExecute(selectAllClockEvents, true).get(0).getResults()) {
				int id = (int)(long) m.get("eid");
				if(!breakers.containsKey(id)) {
					breakers.put(id, new ActiveClockBreaks());
				}
				String type = (String) m.get("clock_event_type");
				ActiveClockBreaks acb = breakers.get(id);
				int hour24 = (int) m.get("clock_event_hour24");
				int min60 = (int) m.get("clock_event_minute60");
				int time = hour24*100+min60;
				if(type.equals("IN")) {
					acb.setClockedIn(time);
				} else if(type.equals("TEN")) {
					acb.updateLast10(time);
				} else if(type.equals("THIRTY")) {
					acb.updateLast30(time);
				}
			}
			List<JSONObject> acs = new LinkedList<JSONObject>();
			for(Map<String,Object> m : DatabaseUtils.prepareAndExecute(selectAllEmployees, true).get(0).getResults()) {
				int id = (int)(long) m.get("eid");
				String name = (String) m.get("name");
				if(breakers.containsKey(id)) {
					JSONObject activeClocks = ResponseServices.getLinkedJsonObject();
					ActiveClockBreaks acb = breakers.get(id);
					acb.calcNextBreaks();
					activeClocks.put("eid", id);
					activeClocks.put("name", name);
					activeClocks.put("time_in", padNum(acb.getInTime(),4));
					if(acb.isLast10CoveredBy30()) {
						activeClocks.put("last_ten", "See 30");
					} else {
						activeClocks.put("last_ten", padNum(acb.getLast10(),4));
					}
					activeClocks.put("last_thirty", padNum(acb.getLast30(),4));
					activeClocks.put("next_ten", padNum(acb.getNext10(),4));
					activeClocks.put("next_thirty", padNum(acb.getNext30(),4));
					int nb = acb.getNext10()>acb.getNext30()?acb.getNext30():acb.getNext10();
					activeClocks.put("next_break", padNum(nb,4));
					boolean added = false;
					for(int i = 0; i < acs.size(); i++) {
						if(acs.get(i).getInt("next_break")>nb) {
							acs.add(i, activeClocks);
							added = true;
							break;
						}
					}
					if(!added) {
						acs.add(activeClocks);
					}
				}
			}
			JSONArray ajs = new JSONArray();
			for(JSONObject j : acs) {
				ajs.put(j);
			}
			response.putData("clocks", ajs);
			response.putDataResponse(ResponseStatus.SUCCESS, "Pulled clock events.");
 			return;
		}
		response.putErrorResponse(InternalErrors.unauthorizedAccessError, "The Ajax service couldn't not validate your access.");
	}
	
	private String padNum(int num, int size) {
		String s = ""+num;
		while(s.length()<size) s = "0" + s;
		return s;
	}
	
}