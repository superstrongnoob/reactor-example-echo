import asyncio
from datetime import datetime

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 14100

MAX_CONNECTIONS = 100
MAX_ROUNDS = 10

MESSAGE = 'message'


async def tcp_echo_client(message, rounds=1):
    reader, writer = await asyncio.open_connection(SERVER_ADDRESS, SERVER_PORT)

    i = 0
    while i < rounds:
        i += 1
        print(f'SEND: {message} {datetime.now().strftime("%M:%S")}')
        writer.write(message.encode())
        await writer.drain()

        data = await reader.read(100)
        print(f'RECV: {data.decode()!r} {datetime.now().strftime("%M:%S")}')

    writer.close()
    await writer.wait_closed()

# 创建一个包含多个tcp_echo_client的列表
tasks = [tcp_echo_client(MESSAGE + str(i), MAX_ROUNDS) for i in range(MAX_CONNECTIONS)]

# 创建一个新的协程函数来执行gather
async def main():
    await asyncio.gather(*tasks)

# 使用run方法运行main协程
asyncio.run(main())
