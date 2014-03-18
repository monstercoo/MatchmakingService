package com.matchmaking.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.NotFoundException;
import com.matchmaking.service.Heartbeat;
import com.matchmaking.service.HeartbeatDatastore;

/**
 * @author Aaron Cook
 *
 *	Heartbeat Service API - This class uses Cloud Endpoints to generate an API to be interfaced with game clients.
 */

@Api(name="heartbeatapi", version="1.0", description="An API to notify the game's server of your match's status")
public class HeartbeatServiceAPI {
 
 
/**
 * registermatch - Called when a client creates a match
 * @param uuid - A unique identifier for this client - platform dependent
 * @param gameMode - Numeric type that represents a game mode
 * @param players - Number of players currently in match
 * @param countrycode - Country code for the country that the Host resides in
 * @return
 * @throws NotFoundException
 */
@ApiMethod(name="registermatch")
public Heartbeat addHeartBeat(HttpServletRequest req, @Named("UUID") String uuid, @Named("gamemode") Integer gameMode, @Named("players") Integer players, @Named("countrycode") String countrycode) throws NotFoundException {
	String key = generateSessionKey();

	Heartbeat h = new Heartbeat(key.toString(), req.getRemoteAddr(), gameMode, players, countrycode, System.currentTimeMillis() / 1000L);

	HeartbeatDatastore.storeHeartbeat(h);
	
	return h;
}

/**
 * endmatch - Ends the current match, called when all players quit. The match can no longer be joined.
 * @param id - the id of the match being ended
 * @throws NotFoundException
 */
@ApiMethod(name="endmatch")
public void endMatch(@Named("id") String id) throws NotFoundException {
	Heartbeat h = new Heartbeat(id);
	HeartbeatDatastore.removeHeartbeat(h);
}

/**
 * ping - Called periodically by the host to notify the service of it's status
 * @param id - id of the Match
 * @param uuid - Unique identifier of the host
 * @param gameMode - Current game mode being played
 * @param players - Number of players in match
 * @param countrycode - Country where the host resides
 * @return
 * @throws NotFoundException
 */
@ApiMethod(name="ping")
public Heartbeat updateHeartbeat(HttpServletRequest req,  @Named("id") String id, @Named("UUID") String uuid, @Named("gamemode") Integer gameMode, @Named("players") Integer players, @Named("countrycode") String countrycode) throws NotFoundException {
	Heartbeat h = new Heartbeat(id, req.getRemoteAddr(), gameMode, players, countrycode, System.currentTimeMillis() / 1000L);
	h.setHostAddress(req.getRemoteAddr());
	HeartbeatDatastore.storeHeartbeat(h);

	return h;
}

/**
 * getOpenMatches - Provides a list of available matches for joining
 */
@ApiMethod(name="getOpenMatches")
public List<Heartbeat> getOpenMatches() {
	return HeartbeatDatastore.getOpenMatches();
}

/**
 * getMostPopularGameMode - Returns the most popular game mode at the moment
 */
@ApiMethod(name="getMostPopularGameMode")
public MostPopularGameMode getHeartbeatsByCountry(@Named("countrycode") String countrycode) {
	MostPopularGameMode mostPopularGameMode = HeartbeatDatastore.getMostPopularGameMode(countrycode);	
	
	return mostPopularGameMode;
}

/**
 * generateSessionKey - Generates a random "session key." This is a simple authentication method, not meant for production
 */
private static String generateSessionKey() {
	String alphabet = new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

	int n = alphabet.length();

	String result = new String();
	Random r = new Random();

	for (int i=0; i<20; i++) {
		result = result + alphabet.charAt(r.nextInt(n));
	}

	return result;
}

}

