package com.matchmaking.service;

/**
 * @author Aaron Cook
 *
 *	Settings - The settings used throughout the Matchmaking Service
 */
public final class Settings {

    private Settings() {

    }

    public static final int MAX_PLAYERS = 16;			// The maximum number of players this game supports
    public static final int CACHE_TTL = 60;				// The number of seconds to cache data in Memcache
    public static final int OPENMATCHES_LIMIT = 10;		// The maximum number of open matches to return to a game client for joining
    public static final long HEARTBEAT_TTL = 30;		// The number of seconds a heartbeat is valid for (a host times out if a new heartbeat isn't received within this time) 

}