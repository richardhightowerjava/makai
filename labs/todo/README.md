# TODO! TADA!

## Overview
TODO is a reference implementation example for Caucho Makai. It is a simple TODO application based on the todomvc.com set.

For clarity, this application is intentionally built with an absolute minimum of external backend dependencies.

The front end uses a popular front end JS Framework called EmberJS, purely for simplicity of clean UI.


## What Does It Do?
Maintains a shared todo list on the client which represents data living in a Makai service on the server.

## Why Should I Care?
This represents a very surface level example of Makai. What you are seeing is a websockets meets Makai actor/message protocol and a
ring buffer managed service.  This means that the client version of these messages is a near-real-time representation. The
implications range wide for interactive applications of nearly infinite variety, massive scaling, and high volume transactional
systems.





