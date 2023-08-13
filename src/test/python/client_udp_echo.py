import asyncio
import socket
import struct
from datetime import datetime


SERVER_ADDRESS = 'localhost'
SERVER_PORT = 14200

MAX_CONNECTIONS = 100
MAX_ROUNDS = 10

MESSAGE = 'message'



class EchoClientProtocol(asyncio.DatagramProtocol):
    def __init__(self, message, rounds, on_con_lost):
        self.message = message
        self.rounds = rounds
        self.index = 0
        self.on_con_lost = on_con_lost
        self.transport = None

    def broadcast(self):
        print(f'SEND: {self.message} {datetime.now().strftime("%M:%S")}')
        self.transport.sendto(self.message.encode(), addr=(SERVER_ADDRESS, SERVER_PORT))

    def connection_made(self, transport):
        self.transport = transport
        self.broadcast()

    def datagram_received(self, data, addr):
        print(f'RECV: {data.decode()!r} {datetime.now().strftime("%M:%S")}')
        self.index += 1
        if self.index < self.rounds:
            self.broadcast()
        else:
            self.transport.close()


    def error_received(self, exc):
        print('error_received ', exc)
        self.on_con_lost.set_result(True)

    def connection_lost(self, exc):
        print('connection_lost')
        self.on_con_lost.set_result(True)



async def main():
    # Get a reference to the event loop as we plan to use
    # low-level APIs.
    loop = asyncio.get_running_loop()

    futurers = []
    transports = []

    for i in range(MAX_CONNECTIONS):
        futurers.append(loop.create_future())

        transport, _ = await loop.create_datagram_endpoint(
            lambda: EchoClientProtocol(
                MESSAGE + str(i),
                MAX_ROUNDS,
                futurers[i]
            ),
            family=socket.AF_INET)
        transports.append(transport)

    for i in range(MAX_CONNECTIONS):
        await futurers[i]


asyncio.run(main())
