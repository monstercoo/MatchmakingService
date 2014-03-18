package com.matchmaking.service;
import com.google.appengine.api.datastore.Entity;

/**
 * @author Aaron Cook
 *
 *	Heartbeat - A heartbeat represents a ping received from a game host, it's the fundamental datatype of the service. 
 *				Heartbeats are received from hosts and stored in the datastore. Old heartbeats from hosts that have timed out
 *				are deleted by the cron job.
 */
public class Heartbeat {
	private String tokenid;
	private String hostaddress;
	private int gameMode;
	private int players;
	private String countrycode;
	private long timestamp;

public Heartbeat(final String token) {
	super();
	this.tokenid = token;
	this.hostaddress = "";
	this.gameMode = 0;
	this.players = 1;
	this.countrycode = "";
	this.timestamp = 0L;
}

public Heartbeat(final Entity entity) {
	super();
	this.tokenid = entity.getKey().getName();
	this.hostaddress = (String) entity.getProperty("hostaddress");
	this.gameMode = new Integer(entity.getProperty("gamemode").toString());
	this.players = new Integer(entity.getProperty("players").toString());
	this.countrycode = (String) entity.getProperty("countrycode");
	this.timestamp = (Long) entity.getProperty("timestamp");
}

public Heartbeat(final String token, final String host,
		final int mode, final int numPlayers, final String countryCode) {

	super();
	this.tokenid = token;
	this.hostaddress = host;
	this.gameMode = mode;
	this.players = numPlayers;
	this.countrycode = countryCode;
	this.timestamp = System.currentTimeMillis() / 1000L;
}

public Heartbeat(final String token, final String host, final int mode,
		final int numPlayers, final String countryCode, final long timeStamp) {
	super();
	this.tokenid = token;
	this.hostaddress = host;
	this.gameMode = mode;
	this.players = numPlayers;
	this.countrycode = countryCode;
	this.timestamp = timeStamp;
}

public final String getId() {
	return tokenid;
}

public final void setId(final String token) {
	this.tokenid = token;
}

public final String getHostAddress() {
	return hostaddress;
}

public final void setHostAddress(final String address) {
	this.hostaddress = address;
}

public final Integer getGameMode() {
	return gameMode;
}

public final void setGameMode(final int mode) {
	this.gameMode = mode;
}

public final Integer getPlayers() {
	return players;
}

public final void setPlayers(final int numPlayers) {
	this.players = numPlayers;
}

public final String getCountryCode() {
	return countrycode;
}

public final void setCountryCode(final String countryCode) {
	this.countrycode = countryCode;
}

public final long getTimeStamp() {
	return timestamp;
}

public final void setTimeStamp(final long time) {
	this.timestamp = time;
}

}