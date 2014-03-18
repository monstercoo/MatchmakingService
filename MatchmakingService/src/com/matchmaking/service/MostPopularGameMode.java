package com.matchmaking.service;

/**
 * @author Aaron Cook
 *
 * MatchStatus - Class for storing the status of an active game match
 */
public class MostPopularGameMode implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String countrycode;
	private long gameMode;
	private long players;

public MostPopularGameMode(final String country, final long mode, final long numPlayers){
	super();
	this.gameMode = mode;
	this.players = numPlayers;
	this.countrycode = country;
}

public final Long getGameMode() {
	return gameMode;
}

public final void setGameMode(final long mode) {
	this.gameMode = mode;
}

public final long getPlayers() {
	return players;
}
 
public final void setPlayers(final long numPlayers) {
	this.players = numPlayers;
}

public final String getCountryCode() {
	return countrycode;
}
 
public final void setCountryCode(final String country) {
	this.countrycode = country;
}
}