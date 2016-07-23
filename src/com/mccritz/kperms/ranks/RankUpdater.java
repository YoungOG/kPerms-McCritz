package com.mccritz.kperms.ranks;

import com.mccritz.kperms.kPerms;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RankUpdater {

    private kPerms main = kPerms.getInstance();
    JedisPool pool = new JedisPool(main.getConfig().getString("database.host"));

    public RankUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                RankUpdaterThread updateThread = new RankUpdaterThread();
                updateThread.run();
            }
        }.runTaskAsynchronously(main);
    }

    public void publish(String message) {

        new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = pool.getResource();
                try {
                    jedis.publish("kperms", message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pool.returnResource(jedis);
                }
            }
        }.runTaskAsynchronously(main);
    }

    public class RankUpdaterThread extends Thread {

        @Override
        public void run() {
            pool.getResource().subscribe(new JedisPubSub() {
                @Override
                public void onUnsubscribe(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onSubscribe(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onPUnsubscribe(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onPSubscribe(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onPMessage(String arg0, String arg1, String arg2) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onMessage(String channel, String message) {
                    if (message == null) {
                        return;
                    }

                    if (message.equalsIgnoreCase("update")) {
                        main.getRankManager().reloadRanks();
                    }
                }
            }, "kperms");
        }
    }

    public void cleanup() {
        pool.destroy();
    }
}
