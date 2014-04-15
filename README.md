# aleph-test

Demonstrates that aleph doesn't respond correctly over ipv4.

    $ lein run
    Starting server
    Started on port 3000

## Ip V6
    $ curl -vvv http://localhost:3000/events

### Response (good)
    * Adding handle: conn: 0x7f943c001a00
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7f943c001a00) send_pipe: 1, recv_pipe: 0
    * About to connect() to localhost port 3000 (#0)
    *   Trying ::1...
    * Connected to localhost (::1) port 3000 (#0)
    > GET /events HTTP/1.1
    > User-Agent: curl/7.30.0
    > Host: localhost:3000
    > Accept: */*
    >
    < HTTP/1.1 200 OK
    * Server aleph/0.3.0 is not blacklisted
    < Server: aleph/0.3.0
    < Date: Tue, 15 Apr 2014 12:27:05 GMT
    < Connection: keep-alive
    < Content-Type: text/event-stream
    < Transfer-Encoding: chunked

The connection remains open, and POST requests to `/trigger` send SSEs over
the open connection.

## Ip V4
    $ curl -vvv http://127.0.0.1:3000/events

### Response (bad)
    * About to connect() to 127.0.0.1 port 3000 (#0)
    *   Trying 127.0.0.1...
    * Adding handle: conn: 0x7f920a004400
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7f920a004400) send_pipe: 1, recv_pipe: 0
    * Connected to 127.0.0.1 (127.0.0.1) port 3000 (#0)
    > GET /events HTTP/1.1
    > User-Agent: curl/7.30.0
    > Host: 127.0.0.1:3000
    > Accept: */*

The connection remains open, but no headers are received by the client. POST
requests to `/trigger` have no effect.
