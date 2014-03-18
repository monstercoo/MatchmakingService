package com.matchmaking.service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aaron Cook
 *
 *	This servelet is meant to be used by a cron job that is called periodically. It removes
 *	old data from the datastore.
 */

@SuppressWarnings("serial")
public class HeartbeatCleanup extends HttpServlet {

	public final void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
			HeartbeatDatastore.cleanupDatastore();
	}

}
