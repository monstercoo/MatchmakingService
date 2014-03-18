package com.matchmaking.service;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.api.server.spi.response.NotFoundException;
import com.matchmaking.service.MostPopularGameMode;
import com.matchmaking.service.Heartbeat;
import com.matchmaking.service.Settings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Aaron Cook
 * HeartBeatDatastore - Class used for all interactions with the Datastore.
 */

/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
/**
 * @author Aaron
 *
 */
public class HeartbeatDatastore {

/**
 * storeHEartbeat - Stores a Heartbeat into the Datastore
 * */
static public void storeHeartbeat(Heartbeat heartbeat) throws NotFoundException
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	Transaction txn = datastore.beginTransaction();
	try {

	    Entity heartbeatEntity = new Entity("Heartbeat", heartbeat.getId());

    	heartbeatEntity.setProperty("gamemode", heartbeat.getGameMode());
    	heartbeatEntity.setProperty("hostaddress", heartbeat.getHostAddress());
	   	heartbeatEntity.setProperty("players", heartbeat.getPlayers());
    	heartbeatEntity.setProperty("countrycode", heartbeat.getCountryCode());
    	heartbeatEntity.setProperty("timestamp", heartbeat.getTimeStamp());

    	datastore.put(heartbeatEntity);

	    txn.commit();
	} finally {
	    if (txn.isActive()) {
	        txn.rollback();
	    }
	}

}

/**
 * removeHeartbeat - Removes a Heartbeat from the datastore
 */
public static void removeHeartbeat(final Heartbeat heartbeat)
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	Transaction txn = datastore.beginTransaction();
	try {
		Key tokenKey = KeyFactory.createKey("Heartbeat", heartbeat.getId());
	    datastore.delete(tokenKey);
	    txn.commit();
	} finally {
	    if (txn.isActive()) {
	        txn.rollback();
	    }
	}
}

/**
 * cleanupDatastore - Removes old (timed out) Heartbeats from the datastore.  
 */
public static void cleanupDatastore() {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	long timestampdelta = (System.currentTimeMillis() / 1000L) - Settings.HEARTBEAT_TTL;
	Filter timestampFilter = new FilterPredicate("timestamp", FilterOperator.LESS_THAN, timestampdelta);	
	Query q = new Query("Heartbeat").setFilter(timestampFilter);

	QueryResultIterator<Entity> iterator = datastore.prepare(q).asQueryResultIterator(FetchOptions.Builder.withChunkSize(500));

	while (iterator.hasNext()) {
	 	datastore.delete(iterator.next().getKey());
	}
}


/**
 * getOpenMatches - Returns a List of open matches currently active
 */
public static List<Heartbeat> getOpenMatches() {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	long timestampdelta = (System.currentTimeMillis() / 1000L) - Settings.HEARTBEAT_TTL;
	Filter timestampFilter = new FilterPredicate("timestamp", FilterOperator.GREATER_THAN_OR_EQUAL, timestampdelta);
	Query q = new Query("Heartbeat").setFilter(timestampFilter);

	List<Entity> heartbeatEntities = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(Settings.OPENMATCHES_LIMIT));
	List<Heartbeat> heartbeats = new ArrayList<Heartbeat>();

	for (Entity heartbeatEntity : heartbeatEntities) {
		if ((long) heartbeatEntity.getProperty("players") < Settings.MAX_PLAYERS)
		{
			heartbeats.add(new Heartbeat(heartbeatEntity));

			if (heartbeats.size() >= Settings.OPENMATCHES_LIMIT) {
				break;
			}
		}
	}

	return heartbeats;
}


/**
 * getMostPopularGameMode - Get's the most popular game mode in a specific region. This value is retrieved from cache if available.
 */
public static MostPopularGameMode getMostPopularGameMode(final String countrycode)
{
	MostPopularGameMode mostPopularGameMode;

	// Attempt to retrieve this query from memcache
	mostPopularGameMode = (MostPopularGameMode) MatchmakingCacheController.get(countrycode);

	if (mostPopularGameMode != null) {
		return mostPopularGameMode;
	}

	// Calculate the most popular gamemode from the data in the datastore.
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	Filter countryFilter = new FilterPredicate("countrycode", FilterOperator.EQUAL, countrycode);

	// Ignore heartbeats older than 90 seconds
	long timestampdelta = (System.currentTimeMillis() / 1000L) - Settings.HEARTBEAT_TTL;

	Filter timestampFilter = new FilterPredicate("timestamp", FilterOperator.GREATER_THAN_OR_EQUAL, timestampdelta);

	Filter heartbeatFilter = CompositeFilterOperator.and(countryFilter, timestampFilter);

	Query q = new Query("Heartbeat").setFilter(heartbeatFilter);

	PreparedQuery pq = datastore.prepare(q);

	LinkedHashMap<Long, Long> gameModePlayerCount = new LinkedHashMap<Long, Long>(15);

	for (Entity result : pq.asIterable()) {

		long gamemode = (long) result.getProperty("gamemode");
		long playercount = (long) result.getProperty("players");

		Long pcount = gameModePlayerCount.get(gamemode);

		if (pcount == null)
		{
			gameModePlayerCount.put(gamemode, playercount);
		} else {
			gameModePlayerCount.put(gamemode, playercount + pcount);
		}

	}

	if (!gameModePlayerCount.isEmpty()) {
		gameModePlayerCount = HeartbeatSort.sortByValue(gameModePlayerCount);
		Map.Entry<Long, Long> firstEntry =	gameModePlayerCount.entrySet().iterator().next();
		mostPopularGameMode = new MostPopularGameMode(countrycode, firstEntry.getKey(), firstEntry.getValue());

		MatchmakingCacheController.put(countrycode, mostPopularGameMode);
	}

	return mostPopularGameMode;
}
}

