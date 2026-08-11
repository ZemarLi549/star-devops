import requests
import time
import random
import json
from concurrent.futures import ThreadPoolExecutor
import multiprocessing
import signal
import sys
import os
import platform
import functools
from redisclient import release_lock,acquire_lock,has_identity_key,set_identity_key,has_identity_json,set_identity_json

# 缓存装饰器

def cached(cache_key_prefix="user", expire_seconds=3600):
    """缓存装饰器，使用Redis缓存方法调用结果"""
    def decorator(func):
        @functools.wraps(func)
        def wrapper(self, *args, **kwargs):
            # 生成缓存键，使用方法名和参数构建唯一键
            if args:
                # 对于get_user_by_user_id，第一个参数是user_id
                key = f"{cache_key_prefix}:{func.__name__}:{args[0]}"
            else:
                key = f"{cache_key_prefix}:{func.__name__}"

            # 检查缓存是否存在
            try:
                # 尝试从缓存获取数据
                cached_data = has_identity_json(key)
                if cached_data:
                    print(f"Cache hit for {key}")
                    return cached_data
            except Exception as e:
                print(f"Error checking cache for {key}: {e}")

            # 缓存未命中，调用原函数
            print(f"Cache miss for {key}, calling function...")
            result = func(self, *args, **kwargs)

            # 将结果存入缓存
            try:
                set_identity_json(key, result, expire_seconds)
                print(f"Result cached for {key} with expiration {expire_seconds}s")
            except Exception as e:
                print(f"Error setting cache for {key}: {e}")

            return result
        return wrapper
    return decorator
app_id = ""
app_secret = ""
class FeishuApier():
    def __init__(self):
        self.app_id = app_id
        self.app_secret = app_secret
        self.token_url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/"

    @cached(cache_key_prefix="user", expire_seconds=3600)
    def get_user_by_user_id(self,user_id):
        """根据user_id获取用户信息，已添加缓存装饰器"""
        # 模拟获取用户信息
        print(f"Fetching user info for user_id: {user_id}")
        # 模拟API延迟
        time.sleep(random.randint(100, 500) / 1000)

        # 返回模拟的用户数据
        return {
            "user_id": user_id,
            "user_name": f"用户{user_id}",
            "email": f"user_{user_id}@example.com",
            "department": "研发部",
            "position": "工程师",
            "created_at": time.strftime("%Y-%m-%d %H:%M:%S")
        }

    def get_user_access_token(self, user_open_id):
        tenant_access_token = ""
        #模拟请求获取用户access_token,加重试三次功能  用break跳出循环
        for _ in range(3):
            try:
                response = requests.post(self.token_url, json={
                    "app_id": self.app_id,
                    "app_secret": self.app_secret,
                    "user_open_id": user_open_id
                },timeout=1)
                if response.status_code == 200:
                    tenant_access_token = response.json().get("tenant_access_token")
                    break
            except Exception as e:
                print(f"get user access token failed, user_open_id: {user_open_id}, error: {e}")
        return tenant_access_token
    def get_users_by_dept(self, dept_id):
        #随机字符串根据部门id获取用户列表
        random_str = str(random.randint(100000, 999999))
        time.sleep(random.randint(800, 3500) / 1000)#随机 延迟 1.2s-4.5s
        #随机  随机rand_num 数量的  user_open_id 组成list
        rand_num = random.randint(1, 85)
        user_open_ids = []
        for _ in range(rand_num):
            user_open_ids.append("ou_" + str(random.randint(1000, 9999)))

        # 使用线程池并发调用，控制并发数为40，不等待返回结果
        max_workers = 40
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            # 批量提交所有任务到线程池，不处理返回结果
            for user_open_id in user_open_ids:
                executor.submit(self.get_user_calendar, user_open_id, {})

        return user_open_ids
    def get_user_calendar(self, user_open_id,userDictInfo = {}):
        try:
            user_token = self.get_user_access_token(user_open_id)
            canlendar_list = [
                {
                    "calendar_id": "10000000000000000000000000000000",
                    "calendar_name": "默认日历",
                    "time_zone": "Asia/Shanghai",
                    "is_primary": True
                }
            ]
            for item in canlendar_list:
                calendar_id = item.get("calendar_id")
                self.get_user_calendar_list(user_token,calendar_id)
        except Exception as e:
            print(f"Error processing user {user_open_id}: {e}")
            # 记录失败的用户ID到文件，使用Redis分布式锁确保多线程安全
            err_file = 'deployed_err_user.txt'
            lock_key = f'lock_err_user_file'
            identifier = acquire_lock(lock_key)
            if identifier:
                try:
                    # 检查用户ID是否已经在错误文件中存在
                    existing_users = set()
                    try:
                        with open(err_file, 'r') as f:
                            existing_users = set(line.strip() for line in f.readlines())
                    except FileNotFoundError:
                        # 文件不存在时创建空集合
                        pass

                    # 如果用户ID不存在，则添加到文件
                    if user_open_id not in existing_users:
                        with open(err_file, 'a') as f:
                            f.write(f"{user_open_id}\n")
                        print(f"Recorded failed user {user_open_id} to {err_file}")
                finally:
                    # 确保锁被释放
                    release_lock(lock_key, identifier)

    def get_user_calendar_list(self, user_token,calendar_id):
        #从events_ids.txt 中随机获取 随机数个 event_ids  组成list
        event_ids = []
        time.sleep(random.randint(800, 3500) / 1000)

        events_file = 'events_ids.txt'
        with open(events_file, 'r') as f:
            lines = f.readlines()
            rand_num = random.randint(1, len(lines))
            event_ids = random.sample(lines, rand_num)
            #随机生成 event_id summary(会议主题)
            event_ids = [{"event_id": event_id.strip(),"summary": f"会议主题{event_id.strip()}"} for event_id in event_ids]
        for eventDict in event_ids:
            event_id = eventDict['event_id']
            flag_pass = False
            identifier = acquire_lock(f'lock_event_id:{event_id}')
            if identifier:   # 如果获取到锁,则执行业务逻辑
                event_id_exists = has_identity_key(f'{event_id}',prefix='get_event_id')
                if event_id_exists:
                    flag_pass = True
                else:
                    set_identity_key(f'{event_id}',24*3600,prefix='get_event_id')
                res = release_lock(f'lock_event_id:{event_id}', identifier)   # 处理完之后释放锁
                print(f'event_id:{event_id},锁释放状态: {res}')
            else:
                print(f'event_id:{event_id},获取redis分布式锁失败, 其他进程正在使用')
            if flag_pass:
                print(f"event_id:{event_id},已处理,跳过")
                continue
            try:
                self.get_calendar_users(user_token,eventDict)
            except Exception as e:
                print(f"Error processing event {event_id}: {e}")
                # 记录失败的eventDict到文件，使用Redis分布式锁确保多线程安全
                err_file = 'deployed_err_event_ids.txt'
                lock_key = f'lock_err_event_file'
                identifier = acquire_lock(lock_key)
                if identifier:
                    try:
                        # 检查event_id是否已经在错误文件中存在
                        existing_events = set()
                        try:
                            with open(err_file, 'r', encoding='utf-8') as f:
                                for line in f.readlines():
                                    try:
                                        # 尝试解析每一行作为JSON
                                        import json
                                        existing_event = json.loads(line.strip())
                                        if 'event_id' in existing_event:
                                            existing_events.add(existing_event['event_id'])
                                    except json.JSONDecodeError:
                                        # 忽略无效的JSON行
                                        continue
                        except FileNotFoundError:
                            # 文件不存在时创建空集合
                            pass

                        # 如果event_id不存在，则添加到文件
                        if event_id not in existing_events:
                            with open(err_file, 'a', encoding='utf-8') as f:
                                import json
                                f.write(json.dumps(eventDict, ensure_ascii=False) + '\n')
                            print(f"Recorded failed event {event_id} to {err_file}")
                    finally:
                        # 确保锁被释放
                        release_lock(lock_key, identifier)
        return event_ids

    def get_calendar_users(self, user_token,event_dict):
        #获取 会议参会人
        event_id = event_dict.get("event_id")
        summary = event_dict.get("summary")
        time.sleep(random.randint(200, 500) / 1000)
        #随机生成 参会用户 rand_num 个
        rand_num = random.randint(1, 10)
        user_open_ids = []
        for _ in range(rand_num):
            user_open_ids.append({
                "user_open_id": "ou_" + str(random.randint(10000, 29999)),
                "user_name": f"用户{random.randint(10000, 29999)}",
                "user_email": f"user{random.randint(10000, 29999)}@example.com"
            })
        for userDict in user_open_ids:
            user_open_id = userDict.get("user_open_id")
            user_name = userDict.get("user_name")
            insert_mysql_dict = {
                "event_id":event_id,
                "summary":summary,
                "user_open_id":user_open_id,
                "user_name":user_name,
            }
            print(insert_mysql_dict)
def process_dept(dept_id):
    """处理单个部门的函数"""
    pid = os.getpid()
    print(f"Process {pid} started processing department {dept_id}")

    # 在Linux上，我们可以更好地处理子进程中的信号
    if platform.system() != 'Windows':
        # 在Linux上，让子进程忽略SIGINT，由主进程统一处理
        signal.signal(signal.SIGINT, signal.SIG_IGN)

    feishu_apier = FeishuApier()
    success = False
    try:
        response = feishu_apier.get_users_by_dept(dept_id)
        print(f"Process {pid} finished department {dept_id}, got {len(response)} users")
        success = True
    except Exception as e:
        print(f"Process {pid} error on department {dept_id}: {e}")
    finally:
        # 处理成功后，将部门ID记录到文件中
        if success:
            try:

                deployed_file = 'deployed_depts.txt'
                # 使用Redis分布式锁确保多进程安全
                lock_key = f'lock_deployed_depts_file:{os.getpid()}'
                identifier = acquire_lock(lock_key)
                if identifier:
                    try:
                        with open(deployed_file, 'a+') as f:
                            # 检查部门ID是否已经存在
                            f.seek(0)
                            existing_depts = [line.strip() for line in f.readlines()]
                            if dept_id not in existing_depts:
                                f.write(f"{dept_id}\n")
                                print(f"Department {dept_id} marked as processed")
                    finally:
                        # 确保锁被释放
                        release_lock(lock_key, identifier)
                        print(f"Process {pid} released lock for file access")
                else:
                    print(f"Process {pid} failed to acquire lock for file access")
            except Exception as e:
                print(f"Error writing to deployed_depts.txt: {e}")

def force_terminate(pool):
    """强制终止进程池"""
    try:
        print("Force terminating all worker processes...")
        # 立即终止所有工作进程
        pool.terminate()
        # 等待进程池完全终止
        pool.join(timeout=5)  # 5秒超时，防止无限等待
        print("All worker processes terminated")
    except Exception as e:
        print(f"Error during termination: {e}")
        # 在极端情况下，直接退出程序
        print("Forcing program exit...")
        # 在Linux上使用os._exit(1)，在Windows上使用sys.exit(1)
        if platform.system() == 'Windows':
            sys.exit(1)
        else:
            os._exit(1)

if __name__ == "__main__":
    # 平台无关的信号处理
    def signal_handler(sig, frame):
        print("\nCtrl+C pressed, initiating forced termination...")
        # 在Linux上，SIGINT处理更可靠
        raise KeyboardInterrupt

    # 设置信号处理器
    signal.signal(signal.SIGINT, signal_handler)

    print("Press Ctrl+C twice quickly to terminate all processes")

    # 从depts_ids.txt读取部门ID
    dept_ids = []
    try:
        depts_file = 'depts_ids.txt'
        with open(depts_file, 'r') as f:
            dept_ids = [line.strip() for line in f.readlines() if line.strip()]
        print(f"Read {len(dept_ids)} department IDs from depts_ids.txt")

        # 读取已处理的部门ID列表
        deployed_depts = []
        deployed_file = 'deployed_depts.txt'
        if os.path.exists(deployed_file):
            with open(deployed_file, 'r') as f:
                deployed_depts = [line.strip() for line in f.readlines() if line.strip()]
            print(f"Read {len(deployed_depts)} already processed departments")

        # 过滤掉已处理的部门
        filtered_dept_ids = [dept_id for dept_id in dept_ids if dept_id not in deployed_depts]
        print(f"Need to process {len(filtered_dept_ids)} departments")

        # 如果没有需要处理的部门，直接退出
        if not filtered_dept_ids:
            print("All departments have been processed. Exiting.")
            sys.exit(0)

        # 使用过滤后的部门列表
        dept_ids = filtered_dept_ids
    except Exception as e:
        print(f"Error reading depts_ids.txt: {e}")
        sys.exit(1)

    # 使用多进程处理部门ID
    num_processes = 5
    print(f"Using {num_processes} processes")

    # 创建进程池
    pool = multiprocessing.Pool(processes=num_processes)

    # 标记是否已经处理过一次Ctrl+C
    interrupt_count = 0

    try:
        # 使用imap_unordered可以更快地响应中断
        # 分批处理，每批10个部门，这样可以更频繁地检查中断
        batch_size = 10
        for i in range(0, len(dept_ids), batch_size):
            batch = dept_ids[i:i+batch_size]
            print(f"Processing batch {i//batch_size + 1} of {len(dept_ids) // batch_size + 1}")

            # 提交当前批次的所有任务
            results = [pool.apply_async(process_dept, (dept_id,)) for dept_id in batch]

            # 等待当前批次完成，但设置较短的超时以便更快响应中断
            for result in results:
                try:
                    # 0.1秒超时，这样可以频繁检查是否有中断
                    result.get(timeout=0.1)
                except multiprocessing.TimeoutError:
                    # 超时不代表错误，继续检查下一个结果
                    pass

    except KeyboardInterrupt:
        print("\nFirst Ctrl+C detected. Initiating termination...")
        interrupt_count += 1
        # 第一次Ctrl+C，尝试正常终止
        force_terminate(pool)

    except Exception as e:
        print(f"Unexpected error: {e}")

    finally:
        # 确保进程池被关闭
        if 'pool' in locals():
            try:
                # 再次尝试终止，确保完全关闭
                pool.terminate()
                pool.join(timeout=3)
                print("Program terminated successfully")
            except:
                # 最后的保障措施
                if platform.system() == 'Windows':
                    sys.exit(0)
                else:
                    os._exit(0)