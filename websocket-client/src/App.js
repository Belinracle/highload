import React, {useState} from 'react';
import {Client} from '@stomp/stompjs';

const SOCKET_URL = 'ws://localhost:8092/ws';

export default function App(props) {
  const [currentUser, setCurrentUser] = useState('')
  const [messageFromServer, setMessageFromServer] = useState('')

  let resolveHeaders = () => {
    return {
      "user": currentUser
    }
  }
  const client = new Client({
    brokerURL: SOCKET_URL,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: () => {
      console.log("stomp client connected")
    },
    onDisconnect: () => {
      console.log("stomp client disconnected")
    },
    connectHeaders: resolveHeaders()
  });
  let onConnectClick = () => {
    client.activate();
    console.log("Connected!!")
    client.subscribe('/users/queue/notifications', function (msg) {
      setMessageFromServer(msg.body)
    });
  }

  let disconnect = () => {
    client.deactivate()
    console.log("Clicked disconnect button")
  }

  return (
      <div>
        <input value={currentUser} onChange={event => {
          setCurrentUser(event.target.value)
        }}/>
        <button onClick={onConnectClick}>Connect</button>
        <button onClick={disconnect}>Disconnect</button>
        <div>{messageFromServer}</div>
      </div>
  );

}