import traceback
import redis
import functools
import json
import time
import uuid
import zlib
import requests
import urllib.parse
from cachetools import TTLCache
from redis.sentinel import Sentinel

# 本地缓存：使用嵌套字典来管理不同的 schema 类型
local_cache = {
    'get_user_info': TTLCache(maxsize=2000, ttl=10),
}
class RedisClient(object):
    def __init__(self,decode_responses=False):
        self.host = "192.168.32.128"
        self.port = 6379
        self.password = "zemarli_549"
        self.master_name = ""
        self.decode_responses = decode_responses
        self.db = 6
        if not decode_responses:decode_responses = False
        self.pool = None
        self.sentinel_hosts = []
        # 判断是否使用 Redis 哨兵
        if ',' in  self.host:
            self.sentinel_hosts = [(host, int(port)) for host, port in (item.split(':') for item in self.host.split(','))]
            self._setup_sentinel()
        else:
            self._setup_single_instance()
    def _setup_sentinel(self):
        """使用 Redis Sentinel 连接池"""
        sentinel = Sentinel(self.sentinel_hosts, socket_timeout=1, password=self.password)
        self.pool = sentinel.master_for(self.master_name, db=self.db, decode_responses=self.decode_responses).connection_pool

    def _setup_single_instance(self):
        """使用 Redis 单机模式连接池"""
        self.pool = redis.ConnectionPool(
            host=self.host,
            port=int(self.port),
            db=self.db,
            password=self.password,
            decode_responses=self.decode_responses,
            socket_connect_timeout=1
        )
    @property
    def client(self):
        return redis.Redis(connection_pool=self.pool)


redis_alarm_cli = RedisClient().client
redis_lock_cli = RedisClient(decode_responses=True).client

def has_identity_key(identity,prefix = 'alarm_identity'):
    has_flag = False
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            if redis_alarm_cli.exists(rk):
                has_flag = True
        except Exception as e:
            print(f'get redis identity key err:{e}')
    return has_flag


def set_identity_key(identity,expire_time,prefix = 'alarm_identity'):
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            redis_alarm_cli.set(rk, 1, ex=expire_time)
        except Exception as e:
            print(f'set redis set_identity_json err:{e}')

#高并发锁
def set_status_key(identity,status=1,expire_time=600,prefix = 'mysqldeploy'):
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            if not expire_time:
                redis_alarm_cli.set(rk, status)
            else:
                redis_alarm_cli.set(rk, status, ex=expire_time)
        except Exception as e:
            print(f'set redis set_status_key err:{e}')

#高并发锁
def has_status_key(identity,prefix = 'mysqldeploy'):
    status = None
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            if redis_alarm_cli.exists(rk):
                status = int(redis_alarm_cli.get(rk).decode('utf-8'))
        except Exception as e:
            print(f'get redis has_status_key err:{e}')
    return status

def set_identity_json(identity,json_info,expire_time=None,prefix = 'alarm_zhiwen'):
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            if not expire_time:
                redis_alarm_cli.set(rk, json.dumps(json_info))
            else:
                redis_alarm_cli.set(rk, json.dumps(json_info), ex=expire_time)
        except Exception as e:
            print(f'set redis set_identity_json err:{e}')
def has_identity_json(identity,prefix = 'alarm_zhiwen'):
    resp_ = None
    if identity:
        rk = f'{prefix}:{identity}'
        try:
            if redis_alarm_cli.exists(rk):
                resp_ = json.loads(redis_alarm_cli.get(rk))
        except Exception as e:
            print(f'get redis identity key err:{e}')
    return resp_

def get_ruleinfo_redis(fingerprint,prefix = 'alarm_finger_rule'):
    resp_ = {}
    if fingerprint:
        rk = f'{prefix}:{fingerprint}'
        try:
            if redis_alarm_cli.exists(rk):
                resp_ = json.loads(redis_alarm_cli.get(rk))
        except Exception as e:
            print(f'getget_ruleinfo_redis key err:{e}')
    return resp_

def common_cache(func,args,kwargs,schema,key_name,expire_time=60*60):
    rk = f'{schema}:{key_name}'
    local_cache_flag = True if schema in local_cache.keys() else False
    # 本地缓存检查
    if key_name in local_cache.get(schema,{}):
        print(f"Cache hit in local cache for {schema} with key_name: {rk}")
        print('mylocal schema>>%s,key_name>>>%s,val:%s'%(schema,key_name,local_cache[schema][key_name]))
        return json.loads(local_cache[schema][key_name])

    # Redis 缓存检查
    try:
        rk = f'{schema}:{key_name}'
        if redis_alarm_cli.exists(rk):
            print(f"Cache hit in Redis for {schema} with key_name: {key_name}")
            compressed_data = redis_alarm_cli.get(rk)
            if 'get_group_' in schema:
                resp_ = json.loads(zlib.decompress(compressed_data).decode('utf-8'))
            else:
                resp_ = json.loads(compressed_data)
            print('redis resp schema>>%s,key_name>>>%s,val:%s'%(schema,key_name,resp_))
            # 同时将 Redis 缓存内容存入本地缓存
            if local_cache_flag:
                print('redis setlocal schema>>%s,key_name>>>%s,val:%s'%(schema,key_name,resp_))
                local_cache[schema][key_name] = json.dumps(resp_)
            return resp_
    except Exception as e:
        print(f'get redis cache {rk} err:{e}')

    # 数据源获取
    print(f"Cache miss for {schema} with key_name: {key_name}, fetching from data source")
    resp_ = func(*args, **kwargs)
    try:
        if 'get_group_' in schema:
            #针对量有可能大的数据进行压缩
            compressed_data = zlib.compress(json.dumps(resp_).encode('utf-8'))
        else:
            compressed_data = json.dumps(resp_)
        redis_alarm_cli.set(rk, compressed_data, ex=expire_time)  # 设置到 Redis，过期时间
    except Exception as e:
        print(f'set redis cache {rk} err:{e}')
    if local_cache_flag:
        print('mysql setlocal schema>>%s,key_name>>>%s,val:%s'%(schema,key_name,resp_))
        #一定要json.dumps 否则会数据丢失 坑
        local_cache[schema][key_name] = json.dumps(resp_)
    return resp_

def redis_cache(schema):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            if schema == 'get_user_info':
                try:
                    username = args[0]
                    if username:
                        return common_cache(func,args,kwargs,schema,username,60*60*6)
                    else:
                        return {}
                except Exception as e:
                    print(f'get_user_info err>>{e}')
                    resp_ = func(*args, **kwargs)
                    return resp_

            else:
                return func(*args, **kwargs)

        return wrapper

    return decorator






@redis_cache('get_user_info')
def get_user_info(username):
    get_res_sql = f'select `username`,`phone`,`email`,`nickname`,`contacts` from users where username=%s'
    res_list = MysqlOp().mysql_dict_query(get_res_sql, [username])
    alarm_dict ={}
    if res_list:
        alarm_dict = res_list[0]
        alarm_dict['contacts'] = json.loads(alarm_dict['contacts'])
    return alarm_dict


# 加锁
def acquire_lock(lock_name, acquire_timeout=120, lock_timeout=180):
    """
    param lock_name: 锁名称
    param acquire_timeout: 客户端获取锁的超时时间
    param lock_timeout: 锁过期时间, 超过这个时间锁自动释放
    """
    identifier = str(uuid.uuid4())
    end_time = time.time() + acquire_timeout   # 客户端获取锁的结束时间
    while time.time() <= end_time:
        # setnx(key, value) 只有 key 不存在情况下将 key 设置为 value 返回 True
        # 若 key 存在则不做任何动作,返回 False
        if redis_lock_cli.setnx(lock_name, identifier):
            redis_lock_cli.expire(lock_name, lock_timeout)   # 设置锁的过期时间，防止线程获取锁后崩溃导致死锁
            return identifier   # 返回锁唯一标识
        elif redis_lock_cli.ttl(lock_name) == -1:   # 当锁未被设置过期时间时，重新设置其过期时间
            redis_lock_cli.expire(lock_name, lock_timeout)
        time.sleep(0.001)
    return False   # 获取超时返回 False


# 释放锁
def release_lock(lock_name, identifier):
    """
    param lock_name:   锁名称
    param identifier:  锁标识
    """
    # 解锁操作需要在一个 redis 事务中进行，python 中 redis 事务通过 pipeline 封装实现
    with redis_lock_cli.pipeline() as pipe:
        while True:
            try:
                # 使用 WATCH 监听锁，如果删除过程中锁自动失效又被其他客户端拿到，即锁标识被其他客户端修改
                # 此时设置了 WATCH 事务就不会再执行，这样就不会出现删除了其他客户端锁的情况
                pipe.watch(lock_name)
                id = pipe.get(lock_name)
                if id and id == identifier:   # 判断解锁与加锁线程是否一致
                    pipe.multi()
                    pipe.delete(lock_name)   # 标识相同，在事务中删除锁
                    pipe.execute()    # 执行EXEC命令后自动执行UNWATCH
                    return True
                pipe.unwatch()
                break
            except redis.WatchError:
                pass
        return False
