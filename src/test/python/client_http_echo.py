import aiohttp
import asyncio
from datetime import datetime

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 14400

MAX_CONNECTIONS = 20
MAX_ROUNDS = 5

MESSAGE = 'message'

url_get = f"http://{SERVER_ADDRESS}:{SERVER_PORT}/http_get_echo"

async def http_get_client(message, rounds=1):
    async with aiohttp.ClientSession() as session:
        i = 0
        while i < rounds:
            i += 1
            print(f'SEND: {message} {datetime.now().strftime("%M:%S")}')
            async with session.get(url_get, params={'message': message}) as resp:
                data = await resp.text()
                print(f'RECV: {data} {datetime.now().strftime("%M:%S")}')


url_post = f"http://{SERVER_ADDRESS}:{SERVER_PORT}/http_post_echo"

async def http_post_client(message, rounds=1):
    async with aiohttp.ClientSession() as session:
        i = 0
        while i < rounds:
            i += 1
            print(f'SEND: {message} {datetime.now().strftime("%M:%S")}')
            async with session.post(url_post, data=message) as resp:
                data = await resp.text()
                print(f'RECV: {data} {datetime.now().strftime("%M:%S")}')


tasks_get = [http_get_client(MESSAGE + str(i), MAX_ROUNDS) for i in range(MAX_CONNECTIONS)]
tasks_post = [http_post_client(MESSAGE + str(i), MAX_ROUNDS) for i in range(MAX_CONNECTIONS)]



async def main():
    await asyncio.gather(*tasks_get)
    await asyncio.gather(*tasks_post)

asyncio.run(main())
