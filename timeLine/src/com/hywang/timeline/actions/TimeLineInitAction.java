package com.hywang.timeline.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.hywang.timeline.DAOFactory;
import com.hywang.timeline.dao.TimlineNodeDAO;
import com.hywang.timeline.entity.TimeLineNode;
import com.hywang.timeline.utils.TimeLineNodeUtil;

public class TimeLineInitAction extends BaseAction {

	private JSONObject timelineObj = null;

	public JSONObject getTimelineObj() {
		return timelineObj;
	}

	public void setTimelineObj(JSONObject timelineObj) {
		this.timelineObj = timelineObj;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5614698496242951219L;

	@Override
	public String execute() throws Exception {
		TimlineNodeDAO timelineDAO = DAOFactory.getInstance()
				.createTimelineNodeDAO();
		List<TimeLineNode> allNodes;
		timelineObj = new JSONObject();
		JSONObject nodes = new JSONObject();
		JSONArray commonNodes = new JSONArray();
		Map nodeProperties = new HashMap();
		try {
			allNodes = timelineDAO.getAllNodes();
			for (TimeLineNode node : allNodes) {
				if (node.isStartNode()) { // if node is start node,init the
											// properties
					/**
					 * "headline":"The Kitchen Sink", "type":"default",
					 * "startDate":"2011,9,1", "text":
					 * "An example of the different kinds of stuff you can do.",
					 **/
					nodeProperties.clear();
					TimeLineNodeUtil.generateTimeLineNodeProperties(
							nodeProperties, node, true);
					nodes.putAll(nodeProperties);
				} else { // if the node is a common node,init every slide
					TimeLineNodeUtil.generateTimeLineNodeProperties(
							nodeProperties, node, false);
					JSONObject commonNode = JSONObject
							.fromObject(nodeProperties);
					commonNodes.add(commonNode);
				}
			}

			nodes.put("date", commonNodes);// finally add all common nodes

			timelineObj.put("timeline", nodes);
			logger.info("Articles initialization success!");
			return SUCCESS;

		} catch (Exception e) {
			logger.error(e);
		} finally {
			logger.info("Articles initialization finished!");
		}
		return ERROR;

	}

}
