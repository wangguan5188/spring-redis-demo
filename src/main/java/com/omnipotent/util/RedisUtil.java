package com.omnipotent.util;

import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 类说明   : redis工具类
 * 作者        :liuys
 * 日期        :2015年8月12日下午4:41:43
 * 版本号    : V1.0
 */
public class RedisUtil {
	
	 private static Logger logger = Logger.getLogger(RedisUtil.class);
	
	private static ShardedJedisPool pool;

	public static ShardedJedisPool getPool() {
		return pool;
	}

	public static void setPool(ShardedJedisPool pool) {
		RedisUtil.pool = pool;
	}

	public final static String KEY_PREFIX = "OPMF_";

	/**
	 * 设置数据
	 * 
	 * @param conn
	 */
	public static boolean setData(String key, String value) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.set( key, value);
			logger.info("redis添加数据key="+key+",value="+value+"成功");
			return true;
		} catch (Exception e) {
			logger.error("redis添加数据key="+key+",value="+value+"失败");
			e.printStackTrace();
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	
	/**
	 * 设置数据
	 * 
	 * @param conn
	 */
	public static boolean setData(String key, List<Object> list) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.set(key.getBytes(), ObjectsTranscoder.serialize(list));
			logger.info("redis添加列表数据key="+key+",value="+list+"成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis添加列表数据key="+key+",value="+list+"失败");
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	
	/**
	 * 设置数据
	 * @param key
	 * @param list
	 * @param timeout 过期时间，单位：秒
	 * @return
	 */
	public static boolean setData(String key, List<Object> list,int timeout) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.set(key.getBytes(), ObjectsTranscoder.serialize(list));
			if(timeout>0){
				jedis.expire(key.getBytes(), timeout);
			}
			logger.info("redis添加列表数据key="+key+",value="+list+"成功,有效期"+timeout+"秒");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis添加列表数据key="+key+",value="+list+"失败,有效期"+timeout+"秒");
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	
	/**
	 * 向key为key的列表末尾添加一个数据
	 * @param key
	 * @param data
	 * @return
	 */
	public static boolean addListData(String key, String data) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.rpush(key, data);
			
			logger.info("redis添加列表数据key="+key+",value="+data+"成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis添加列表数据key="+key+",value="+data+"失败");
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	
	/**
	 * 获取列表数据
	 * @param key
	 * @return
	 */
	public static List<String> getList(String key){
		ShardedJedis jedis = null;
		List<String> list = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			long end = jedis.llen(key);
			list=jedis.lrange(key, 0, end);
			
			logger.info("redis获取列表数据key="+key+",value="+list+"成功");
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis获取列表数据key="+key+",value="+list+"失败");
			return null;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	/**
	 * 从redis 获取LIST列表信息
	 * @param key
	 * @return
	 */
	public static List<Object> getListData(String key) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			//jedis.set( key, value);
			byte[] in = jedis.get(key.getBytes());  
			List<Object> list = ObjectsTranscoder.deserialize(in);  
			logger.info("redis获取列表数据key="+key+"成功");
			return list;
		} catch (Exception e) {
			logger.error("redis获取列表数据key="+key+"失败");
			e.printStackTrace();
			return null;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}
	
	/**
	 * 设置数据，带过期时间，未来多少秒过期
	 * @param key
	 * @param value
	 * @param timeout
	 * @return
	 */
	public static boolean setData(String key, String value,int timeout) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.set( key, value);
			if(timeout>0){
				jedis.expire(key, timeout);
			}
			logger.info("redis添加限时数据key="+key+",value="+value+"成功,有效期"+timeout+"秒");

			return true;
		} catch (Exception e) {
			logger.error("redis添加限时数据key="+key+",value="+value+"失败,有效期"+timeout+"秒");
			e.printStackTrace();
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param conn
	 */
	public static String getData(String key) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		String value = "";
		try {
			jedis = pool.getResource();
			value = jedis.get( key);
			logger.info("redis获取数据key="+key+",value="+value+"成功");

		} catch (Exception e) {
			logger.error("redis获取数据key="+key+",value="+value+"失败");

			e.printStackTrace();
		}finally{
			if(jedis!=null)
			jedis.close();
		}
		return value;
	}

	/**
	 * 删除数据
	 * 
	 * @param key
	 * @return
	 */
	public static boolean deleteData(String key) {
		ShardedJedis jedis = null;
		key =KEY_PREFIX+key;
		try {
			jedis = pool.getResource();
			jedis.del(key);
			logger.info("redis删除数据key="+key+"成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis删除数据key="+key+"失败");
			return false;
		}finally{
			if(jedis!=null)
			jedis.close();
		}
	}

	/**
	 * 清空redis数据
	 * @return
	 */
	public static boolean flushAll(){
		// 此功能待开发
		return false;
	}
	
	/**
	 * <p>将给定值（一个或多个）推入列表的左端.</p>
	 * 
	 * <pre>
	 * RedisUtil.lpush("key", "value1");
	 * RedisUtil.lpush("key", "value2");
	 * </pre>
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("deprecation")
	public static void lpush(String key, String value){
		ShardedJedis jedis = pool.getResource();
		key = KEY_PREFIX+key;
		jedis.lpush(key, value);
		pool.returnResource(jedis);
	}
	
	/**
     * <p>移除并返回列表最右端的元素.</p>
     *
     * <pre>
     * RedisUtil.rpop(null) = null;
     * </pre>
     *
	 * @param key 键
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String rpop(String key) {
		ShardedJedis jedis = pool.getResource();
		key = KEY_PREFIX+key;
		String msg = jedis.rpop(key);
		pool.returnResource(jedis);
		return msg;
	}
	
	/**
	 * <p>查看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1</p>
	 * <pre>
	 * Long seconds = RedisUtil.getExpirationTime(key);
	 * </pre>
	 * @author zhangtb
	 * @date 2016-04-07 16:20:51
	 * @param key
	 * @return
	 */
	public static Long getExpirationTime(String key) {
		ShardedJedis jedis = pool.getResource();
		key = KEY_PREFIX + key;
		Long seconds = jedis.ttl(key);
		return seconds;
	}
	
	/**
	 * 判断key否存在
	 * @author zhangtb
	 * @date 2016-04-07 16:27:51
	 * @param key
	 * @return
	 */
	public static Boolean isExists(String key) {
		ShardedJedis jedis = pool.getResource();
		return jedis.exists(key);
	}
	
	/**
	 * 输出系统中所有的key
	 * @return
	 */
	public static void keys() {
		//TODO
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.1.101");
		/*//jedis 排序
		//注意，此处的rpush和lpush是List的操作。是一个双向链表（但从表现来看的）
		jedis.del("a");// 先清除数据，再加入数据进行测试
		jedis.rpush("a", "1");// 将给定值（一个或多个）推入列表的右端
		jedis.rpush("a", "2");
		jedis.rpush("a", "3");
		jedis.rpush("a", "4");
		jedis.lpush("a", "6");// 将给定值（一个或多个）推入列表的左端
		jedis.lpush("a", "7");
		jedis.lpush("a", "8");
		jedis.lpush("a", "9");
		System.out.println(jedis.lrange("a", 0, -1));// [9, 3, 6, 1]获取列表在给定范围上的所有值
		System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //输入排序后结果
		System.out.println(jedis.lrange("a", 0, -1));
		System.out.println(jedis.ltrim("a", 0, 6));// 对列表进行修剪，只保留从start偏移量到end偏移量范围内的元素
		System.out.println(jedis.lindex("a", 0));// 获取列表在给定位置上（偏移量为offset）的单个元素
		System.out.println(jedis.lpop("a"));// 从列表的左端弹出一个值，并返回被弹出的值
		System.out.println(jedis.rpop("a"));// 移除并返回列表最右端的元素
		*/		
		// 看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1
		jedis.set("034861823283", "34861823283");
		System.out.println(jedis.get("034861823283"));
		
		/*Set<String> keys = jedis.keys("*");
        Iterator<String> it=keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            System.out.println(key);
        }*/
	}
	
}
