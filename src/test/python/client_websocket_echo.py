import asyncio
import websockets
from datetime import datetime

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 14300

MAX_CONNECTIONS = 100
MAX_ROUNDS = 10

MESSAGE = 'message'

uri = f"ws://{SERVER_ADDRESS}:{SERVER_PORT}/websocket_echo"

async def websocket_echo_client(message, rounds=1):
    try:
        async with websockets.connect(uri) as websocket:
            i = 0
            while i < rounds:
                i += 1
                print(f'SEND: {message} {datetime.now().strftime("%M:%S")}')
                await websocket.send(message)

                data = await websocket.recv()
                print(f'RECV: {data} {datetime.now().strftime("%M:%S")}')

    except websockets.exceptions.ConnectionClosed:
        print("Client disconnected")



tasks = [websocket_echo_client(MESSAGE + str(i), MAX_ROUNDS) for i in range(MAX_CONNECTIONS)]

# 创建一个新的协程函数来执行gather
async def main():
    await asyncio.gather(*tasks)

# 使用run方法运行main协程
asyncio.run(main())

