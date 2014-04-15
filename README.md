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

# TCP Dump
    sudo tcpdump -i lo0 -A tcp port 3000

    tcpdump: verbose output suppressed, use -v or -vv for full protocol decode
    listening on lo0, link-type NULL (BSD loopback), capture size 65535 bytes

## IPv4 Capture (bad)
    13:45:42.864334 IP localhost.56351 > localhost.hbci: Flags [S], seq 368205316, win 65535, options [mss 16344,nop,wscale 4,nop,nop,TS val 422479165 ecr 0,sackOK,eol], length 0
    E..@.P@.@.................^..........4....?........
    ...=........
    13:45:42.864404 IP localhost.hbci > localhost.56351: Flags [S.], seq 2771339529, ack 368205317, win 65535, options [mss 16344,nop,wscale 4,nop,nop,TS val 422479166 ecr 422479165,sackOK,eol], length 0
    E..@;.@.@................/I	..^......4....?........
    ...>...=....
    13:45:42.864419 IP localhost.56351 > localhost.hbci: Flags [.], ack 1, win 9186, options [nop,nop,TS val 422479166 ecr 422479166], length 0
    E..4..@.@.................^../I
    ..#..(.....
    ...>...>
    13:45:42.864433 IP localhost.hbci > localhost.56351: Flags [.], ack 1, win 9186, options [nop,nop,TS val 422479166 ecr 422479166], length 0
    E..4.:@.@................/I
    ..^...#..(.....
    ...>...>
    13:45:42.865194 IP localhost.56351 > localhost.hbci: Flags [P.], seq 1:85, ack 1, win 9186, options [nop,nop,TS val 422479166 ecr 422479166], length 84
    E...\.@.@.................^../I
    ..#..|.....
    ...>...>GET /events HTTP/1.1
    User-Agent: curl/7.30.0
    Host: 127.0.0.1:3000
    Accept: */*


    13:45:42.865216 IP localhost.hbci > localhost.56351: Flags [.], ack 85, win 9181, options [nop,nop,TS val 422479166 ecr 422479166], length 0
    E..4._@.@................/I
    ..^Y..#..(.....
    ...>...>
    13:45:42.867741 IP localhost.hbci > localhost.56351: Flags [P.], seq 1:163, ack 85, win 9181, options [nop,nop,TS val 422479168 ecr 422479166], length 162
    E.....@.@................/I
    ..^Y..#........
    ...@...>HTTP/1.1 200 OK
    Server: aleph/0.3.0
    Date: Tue, 15 Apr 2014 12:45:42 GMT
    Connection: keep-alive
    Content-Type: text/event-stream
    Transfer-Encoding: chunked


    13:45:42.867763 IP localhost.56351 > localhost.hbci: Flags [.], ack 163, win 9176, options [nop,nop,TS val 422479168 ecr 422479168], length 0
    E..4..@.@.................^Y./I...#..(.....
    ...@...@
    13:45:48.114168 IP localhost.56351 > localhost.hbci: Flags [F.], seq 85, ack 163, win 9176, options [nop,nop,TS val 422484393 ecr 422479168], length 0
    E..4..@.@.................^Y./I...#..(.....
    .......@
    13:45:48.114201 IP localhost.hbci > localhost.56351: Flags [.], ack 86, win 9181, options [nop,nop,TS val 422484393 ecr 422484393], length 0
    E..4s.@.@................/I...^Z..#..(.....
    ........
    13:45:48.114213 IP localhost.56351 > localhost.hbci: Flags [.], ack 163, win 9176, options [nop,nop,TS val 422484393 ecr 422484393], length 0
    E..4\.@.@.................^Z./I...#..(.....
    ........
    13:45:48.114351 IP localhost.hbci > localhost.56351: Flags [F.], seq 163, ack 86, win 9181, options [nop,nop,TS val 422484393 ecr 422484393], length 0
    E..4.p@.@................/I...^Z..#..(.....
    ........
    13:45:48.114380 IP localhost.56351 > localhost.hbci: Flags [.], ack 164, win 9176, options [nop,nop,TS val 422484393 ecr 422484393], length 0
    E..4w.@.@.................^Z./I...#..(.....
    ........

## IPv6 Capture (good)


    13:46:14.040507 IP6 localhost.56355 > localhost.hbci: Flags [S], seq 2979483123, win 65535, options [mss 16324,nop,wscale 4,nop,nop,TS val 422510184 ecr 0,sackOK,eol], length 0
    `....,.@.................................#....M..........4....?........
    ...h........
    13:46:14.040566 IP6 localhost.hbci > localhost.56355: Flags [S.], seq 4042907490, ack 2979483124, win 65535, options [mss 16324,nop,wscale 4,nop,nop,TS val 422510184 ecr 422510184,sackOK,eol], length 0
    `..,.@...................................#...b..M......4....?........
    ...h...h....
    13:46:14.040578 IP6 localhost.56355 > localhost.hbci: Flags [.], ack 1, win 9175, options [nop,nop,TS val 422510184 ecr 422510184], length 0
    `.... .@.................................#....M....c..#..(.....
    ...h...h
    13:46:14.040588 IP6 localhost.hbci > localhost.56355: Flags [.], ack 1, win 9175, options [nop,nop,TS val 422510184 ecr 422510184], length 0
    `.. .@...................................#...c..M...#..(.....
    ...h...h
    13:46:14.040613 IP6 localhost.56355 > localhost.hbci: Flags [P.], seq 1:85, ack 1, win 9175, options [nop,nop,TS val 422510184 ecr 422510184], length 84
    `....t.@.................................#....M....c..#..|.....
    ...h...hGET /events HTTP/1.1
    User-Agent: curl/7.30.0
    Host: localhost:3000
    Accept: */*


    13:46:14.040639 IP6 localhost.hbci > localhost.56355: Flags [.], ack 85, win 9170, options [nop,nop,TS val 422510184 ecr 422510184], length 0
    `.. .@...................................#...c..NH..#..(.....
    ...h...h
    13:46:14.043905 IP6 localhost.hbci > localhost.56355: Flags [P.], seq 1:163, ack 85, win 9170, options [nop,nop,TS val 422510186 ecr 422510184], length 162
    `....@...................................#...c..NH..#........
    ...j...hHTTP/1.1 200 OK
    Server: aleph/0.3.0
    Date: Tue, 15 Apr 2014 12:46:14 GMT
    Connection: keep-alive
    Content-Type: text/event-stream
    Transfer-Encoding: chunked


    13:46:14.043942 IP6 localhost.56355 > localhost.hbci: Flags [.], ack 163, win 9165, options [nop,nop,TS val 422510186 ecr 422510186], length 0
    `.... .@.................................#....NH......#..(.....
    ...j...j
    13:46:19.014832 IP6 localhost.56355 > localhost.hbci: Flags [F.], seq 85, ack 163, win 9165, options [nop,nop,TS val 422515140 ecr 422510186], length 0
    `.... .@.................................#....NH......#..(.....
    ./.....j
    13:46:19.014869 IP6 localhost.hbci > localhost.56355: Flags [.], ack 86, win 9170, options [nop,nop,TS val 422515140 ecr 422515140], length 0
    `.. .@...................................#......NI..#..(.....
    ./.../..
    13:46:19.014881 IP6 localhost.56355 > localhost.hbci: Flags [.], ack 163, win 9165, options [nop,nop,TS val 422515140 ecr 422515140], length 0
    `.... .@.................................#....NI......#..(.....
    ./.../..
    13:46:19.015002 IP6 localhost.hbci > localhost.56355: Flags [F.], seq 163, ack 86, win 9170, options [nop,nop,TS val 422515140 ecr 422515140], length 0
    `.. .@...................................#......NI..#..(.....
    ./.../..
    13:46:19.015027 IP6 localhost.56355 > localhost.hbci: Flags [.], ack 164, win 9165, options [nop,nop,TS val 422515140 ecr 422515140], length 0
    `.... .@.................................#....NI......#..(.....
    ./.../..
