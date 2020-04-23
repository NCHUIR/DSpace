package org.dspace.statistics;

import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemWithBitstreamVsTotalCounter implements Runnable
{
    /** log4j category */
    private static Logger log = Logger.getLogger(ItemWithBitstreamVsTotalCounter.class);

    // Configs and settings
    private static String bundle_name = "ORIGINAL";
    private static long updateInterval = 43200000; // default is 12 hours

    // Storage
    private static ItemWithBitstreamVsTotalCounter siteCount;
    private static Map<Integer, ItemWithBitstreamVsTotalCounter> communityCount;
    private static Map<Integer, ItemWithBitstreamVsTotalCounter> collectionCount;

    // Service startup
    static {
        lastUpdate = 0;
        updateInterval = ConfigurationManager.getIntProperty("ItemWithBitstreamVsTotalCounter.updateInterval",(int) updateInterval);
        try{
            log.info("update at init ... ");
            update(new Context());
        }catch(Exception e){
            log.error("Error at service startup");
            log.error(e.toString());
        }
    }

    // the getting APIs

    public static ItemWithBitstreamVsTotalCounter getSiteCount() {
        return siteCount == null ? new ItemWithBitstreamVsTotalCounter() : siteCount;
    }

    public static ItemWithBitstreamVsTotalCounter getCommunityCount(Community community) {
        return getCommunityCount(community.getID());
    }

    public static ItemWithBitstreamVsTotalCounter getCommunityCount(int community_id) {
        ItemWithBitstreamVsTotalCounter r = communityCount.get(community_id);
        if(r == null) return new ItemWithBitstreamVsTotalCounter();
        else return r;
    }

    public static ItemWithBitstreamVsTotalCounter getCollectionCount(Collection collection) {
        return getCollectionCount(collection.getID());
    }

    public static ItemWithBitstreamVsTotalCounter getCollectionCount(int collection_id) {
        ItemWithBitstreamVsTotalCounter r = collectionCount.get(collection_id);
        if(r == null) return new ItemWithBitstreamVsTotalCounter();
        else return r;
    }

    // Update and calculate

    private static long lastUpdate;
    public static synchronized void update(Context context) {
        log.debug("checking if update is required ...");
        long current = System.currentTimeMillis();
        log.debug(String.format(" current = %d, lastUpdate = %d, inteval = %d, updateInterval = %d", current, lastUpdate, current - lastUpdate, updateInterval));
        if((current - lastUpdate) > updateInterval){
            lastUpdate = current;
        	(new Thread(new ItemWithBitstreamVsTotalCounter(context))).run();
	    }
    }

    /* ==============================
    ItemWithBitstreamVsTotalCounter Instance
    for:
    1. Updating value
    2. Data structure for number of item with bitstream / number of items
    */

    // Data structure part

    private int number_of_bs = 0;
    private int number_of_items = 0;

    public ItemWithBitstreamVsTotalCounter() {
    }

    public ItemWithBitstreamVsTotalCounter(long number_of_bs, long number_of_items) {
        this.number_of_bs = (int) number_of_bs;
        this.number_of_items = (int) number_of_items;
    }
    
    public ItemWithBitstreamVsTotalCounter(int number_of_bs, int number_of_items) {
        this.number_of_bs = number_of_bs;
        this.number_of_items = number_of_items;
    }

    public ItemWithBitstreamVsTotalCounter(ItemWithBitstreamVsTotalCounter tmpCnt) {
        this.number_of_bs = tmpCnt.number_of_bs;
        this.number_of_items = tmpCnt.number_of_items;
    }

    public int getNumberOfBS() {
        return this.number_of_bs;
    }

    public int getNumberOfItems() {
        return this.number_of_items;
    }

    public String toString() {
        return String.format("%d/%d", this.number_of_bs, this.number_of_items);
    }

    // Updating part

    private Context context;

    public ItemWithBitstreamVsTotalCounter(Context context) {
        this.context = context;
    }

    private static final String col_collection_id = "collection_id";
    private static final String col_number_of_bs = "number_of_bs";
    private static final String col_number_of_items = "number_of_items";

    @Override
    public void run() {
        /*
        =======================================
        SQL Query to fetch all collections with their item count:

        SELECT a.owning_collection AS collection_id, count(a) AS number_of_items
        FROM item AS a
        WHERE a.in_archive IS TRUE AND a.withdrawn IS FALSE AND a.discoverable IS TRUE
        GROUP BY a.owning_collection;

        =======================================
        SQL Query to count all items and items with bitstream (query site-item-bs-count):

        SELECT count(a) AS number_of_items, count(g) AS number_of_bs
        FROM item AS a
        LEFT JOIN (
                SELECT b.item_id,MAX(b.bundle_id),MAX(c.primary_bitstream_id) FROM item2bundle AS b
                LEFT JOIN bundle AS c ON b.bundle_id = c.bundle_id WHERE c.name = 'ORIGINAL' GROUP BY b.item_id
        ) AS d (item_id,bundle_id,primary_bitstream_id)
        ON a.in_archive IS TRUE AND a.withdrawn IS FALSE AND a.discoverable IS TRUE AND a.item_id = d.item_id
        LEFT JOIN (
                SELECT e.bundle_id,MAX(e.bitstream_id) FROM bundle2bitstream AS e GROUP BY e.bundle_id
        ) AS f (bundle_id,bitstream_id)
        ON d.bundle_id = f.bundle_id
        LEFT JOIN bitstream AS g
        ON g.deleted IS FALSE AND ((d.primary_bitstream_id IS NULL AND f.bitstream_id = g.bitstream_id) OR d.primary_bitstream_id = g.bitstream_id);

        The result of the above query look like this:

         number_of_items | number_of_bs
        -----------------+--------------
                   68634 |        11539
        (1 row)

        =======================================
        SQL Query to fetch all collections with their item count and bitstream count (query collection-item-bs-count):

        SELECT h.collection_id AS collection_id, count(a) AS number_of_items, count(g) AS number_of_bs
        FROM collection AS h
        LEFT JOIN collection2item AS i
        ON h.collection_id = i.collection_id
        LEFT JOIN item AS a
        ON i.item_id = a.item_id
        LEFT JOIN (
                SELECT b.item_id,MAX(b.bundle_id),MAX(c.primary_bitstream_id) FROM item2bundle AS b
                LEFT JOIN bundle AS c ON b.bundle_id = c.bundle_id WHERE c.name = 'ORIGINAL' GROUP BY b.item_id
        ) AS d (item_id,bundle_id,primary_bitstream_id)
        ON a.in_archive IS TRUE AND a.withdrawn IS FALSE AND a.discoverable IS TRUE AND a.item_id = d.item_id
        LEFT JOIN (
                SELECT e.bundle_id,MAX(e.bitstream_id) FROM bundle2bitstream AS e GROUP BY e.bundle_id
        ) AS f (bundle_id,bitstream_id)
        ON d.bundle_id = f.bundle_id
        LEFT JOIN bitstream AS g
        ON g.deleted IS FALSE AND ((d.primary_bitstream_id IS NULL AND f.bitstream_id = g.bitstream_id) OR d.primary_bitstream_id = g.bitstream_id)
        GROUP BY h.collection_id;

        The result of the above query look like this:

         collection_id | number_of_items | number_of_bs
        ---------------+-----------------+--------------
                     3 |            2177 |           52
                     4 |             251 |           19
                     5 |            1932 |           16
                     7 |            1719 |           49
                     8 |             172 |            7
                     9 |             676 |           28
                    10 |             180 |            9
        ................... [more] .....................

        =======================================
        */

        // Calculate each collection's item count and bitstream count from the whole item table, calculate count for all communities and the ste then save to the static variable
    	StringBuilder debugMsg = new StringBuilder();
        try {
            if(context == null)
                throw new Exception("context is null, refused to run");
		    log.info("ItemWithBitstreamVsTotalCounter starting to update ...");

            // build the above query site-item-bs-count:
			StringBuilder query = new StringBuilder();
            query.append("SELECT count(a) AS number_of_items, count(g) AS number_of_bs FROM item AS a LEFT JOIN ( SELECT b.item_id,MAX(b.bundle_id),MAX(c.primary_bitstream_id) FROM item2bundle AS b LEFT JOIN bundle AS c ON b.bundle_id = c.bundle_id ");
            query.append(" GROUP BY b.item_id ) AS d (item_id,bundle_id,primary_bitstream_id) ON a.in_archive IS TRUE AND a.withdrawn IS FALSE AND a.discoverable IS TRUE AND a.item_id = d.item_id LEFT JOIN ( SELECT e.bundle_id,MAX(e.bitstream_id) FROM bundle2bitstream AS e GROUP BY e.bundle_id ) AS f (bundle_id,bitstream_id) ON d.bundle_id = f.bundle_id LEFT JOIN bitstream AS g ON g.deleted IS FALSE AND ((d.primary_bitstream_id IS NULL AND f.bitstream_id = g.bitstream_id) OR d.primary_bitstream_id = g.bitstream_id);");

            if(log.isDebugEnabled()){
                debugMsg.append("Executing query site-item-bs-count: \n");
                debugMsg.append(query);
            }

            TableRowIterator tri = DatabaseManager.query(context,query.toString());
            TableRow r;
            long number_of_bs;
            long number_of_items;

            ItemWithBitstreamVsTotalCounter siteCount = new ItemWithBitstreamVsTotalCounter();

            if(tri.hasNext()){
                r = tri.next();
                number_of_bs = r.isColumnNull(col_number_of_bs) ? 0 : r.getLongColumn(col_number_of_bs);
                number_of_items = r.isColumnNull(col_number_of_items) ? 0 : r.getLongColumn(col_number_of_items);
                siteCount.number_of_bs = (int) number_of_bs;
                siteCount.number_of_items = (int) number_of_items;
                if(log.isDebugEnabled()){
                    debugMsg.append("Query site-item-bs-count result: \n number_of_items | number_of_bs\n-----------------+--------------\n");
                    debugMsg.append(String.format(" %15d | %12d \n",number_of_items,number_of_bs));
                }
            } else {
                log.warn("There is an empty response from db when query site-item-bs-count");
            }

            // build the above query collection-item-bs-count:
            query = new StringBuilder();
			query.append("SELECT h.collection_id AS collection_id, count(a) AS number_of_items, count(g) AS number_of_bs FROM collection AS h LEFT JOIN collection2item AS i ON h.collection_id = i.collection_id LEFT JOIN item AS a ON i.item_id = a.item_id LEFT JOIN ( SELECT b.item_id,MAX(b.bundle_id),MAX(c.primary_bitstream_id) FROM item2bundle AS b LEFT JOIN bundle AS c ON b.bundle_id = c.bundle_id ");
            query.append(" GROUP BY b.item_id ) AS d (item_id,bundle_id,primary_bitstream_id) ON a.in_archive IS TRUE AND a.withdrawn IS FALSE AND a.discoverable IS TRUE AND a.item_id = d.item_id LEFT JOIN ( SELECT e.bundle_id,MAX(e.bitstream_id) FROM bundle2bitstream AS e GROUP BY e.bundle_id ) AS f (bundle_id,bitstream_id) ON d.bundle_id = f.bundle_id LEFT JOIN bitstream AS g ON g.deleted IS FALSE AND ((d.primary_bitstream_id IS NULL AND f.bitstream_id = g.bitstream_id) OR d.primary_bitstream_id = g.bitstream_id) GROUP BY h.collection_id;");

            if(log.isDebugEnabled()){
                debugMsg.append("Executing query collection-item-bs-count: \n");
                debugMsg.append(query);
            }

            tri = DatabaseManager.query(context,query.toString());

			if(!tri.hasNext()){
				log.warn("There is an empty response from db when query collection-item-bs-count");
                return;
            }

            Map<Integer, ItemWithBitstreamVsTotalCounter> communityCount = new HashMap<Integer, ItemWithBitstreamVsTotalCounter>();
            Map<Integer, ItemWithBitstreamVsTotalCounter> collectionCount = new HashMap<Integer, ItemWithBitstreamVsTotalCounter>();
            
            if(log.isDebugEnabled())
			    debugMsg.append("\nQuery collection-item-bs-count result:\n collection_id | number_of_items | number_of_bs\n---------------+-----------------+--------------\n");

            int collection_id;
            Community[] communities;
            ItemWithBitstreamVsTotalCounter tmpCnt;
            while(tri.hasNext()){
                r = tri.next();
                collection_id = r.isColumnNull(col_collection_id) ? -1 : r.getIntColumn(col_collection_id);
                number_of_bs = r.isColumnNull(col_number_of_bs) ? 0 : r.getLongColumn(col_number_of_bs);
                number_of_items = r.isColumnNull(col_number_of_items) ? 0 : r.getLongColumn(col_number_of_items);

                if(log.isDebugEnabled())
                    debugMsg.append(String.format(" %13d | %15d | %12d \n",collection_id,number_of_items,number_of_bs));

                if(collection_id == -1) continue;

                tmpCnt = new ItemWithBitstreamVsTotalCounter(number_of_bs,number_of_items);
                collectionCount.put(collection_id,tmpCnt);

                try {
                    communities = Collection.find(context,collection_id).getCommunities();
                    countCommunityRecursively(communities,communityCount,tmpCnt);
                } catch (NullPointerException e) {
                    log.warn("There is no Collection that collection_id = " + collection_id);
                    log.debug(e);
                } catch (Exception e) {
                    log.error("There is an error when collection_id = " + collection_id);
                    log.error(e);
                }
            }

            if(log.isDebugEnabled())
			    log.debug(debugMsg.toString());
			log.info(String.format(
                "ItemWithBitstreamVsTotalCounter updated, there are %d bitstreams / %d items in the whole site, %d of communityCount was built, %d of collectionCount was built",
                siteCount.getNumberOfBS(),
                siteCount.getNumberOfItems(),
                communityCount.size(),
                collectionCount.size()
            ));

            ItemWithBitstreamVsTotalCounter.siteCount = siteCount;
            ItemWithBitstreamVsTotalCounter.communityCount = communityCount;
            ItemWithBitstreamVsTotalCounter.collectionCount = collectionCount;
        } catch (Exception e) {
			log.error(e.toString());
			log.error(debugMsg.toString());
        }
    }

    private void countCommunityRecursively(Community[] communities, Map<Integer, ItemWithBitstreamVsTotalCounter> communityCount, ItemWithBitstreamVsTotalCounter addCnt) throws SQLException {
        for (Community community: communities) {
            countCommunity(community,communityCount,addCnt);

            Community[] parentCommunities = community.getAllParents();
            for (Community upperCommunity: parentCommunities)
                countCommunity(upperCommunity,communityCount,addCnt);
        }
    }

    private void countCommunity(Community community, Map<Integer, ItemWithBitstreamVsTotalCounter> communityCount, ItemWithBitstreamVsTotalCounter addCnt) throws SQLException {
        ItemWithBitstreamVsTotalCounter tmpCnt = communityCount.get(community.getID());
        if(tmpCnt != null){
            tmpCnt.number_of_bs += addCnt.number_of_bs;
            tmpCnt.number_of_items += addCnt.number_of_items;
        }
        else {
            communityCount.put(community.getID(),new ItemWithBitstreamVsTotalCounter(addCnt));
        }
    }
}
