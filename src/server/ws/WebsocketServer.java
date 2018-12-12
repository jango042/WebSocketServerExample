package server.ws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;




@ServerEndpoint(
        value="/chat/{username}",//value="/chat/{username}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class WebsocketServer {
	
	private final Logger log = Logger.getLogger(getClass().getName());

    private Session session;
    private String username;
    private static final Set<WebsocketServer> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String,String> users = new HashMap<>();
 // set to store all the live sessions
    private static final Set<Session> sessions = Collections
            .synchronizedSet(new HashSet<Session>());
 
    // Mapping between session and person name
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
    private JSONUtils jsonUtils = new JSONUtils();
    
 
    
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        log.info(session.getId() + " connected!");

        this.session = session;
        this.username = username;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("connected!");
        
        String msg = jsonUtils.getClientDetailsJson(session.getId(),
                "Your session details");
        
        //from a different app
        /*
         * try {
            // Sending session id to the client that just connected
            session.getBasicRemote().sendText(
                    jsonUtils.getClientDetailsJson(session.getId(),
                            "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }
         * */
        //from a different app
        //broadcast(msg);
        broadcast(message);
    }
    
    @OnMessage
    public void onMessage(Session session, String messages) throws IOException, EncodeException {
        log.info("My log: "+messages.toString());

        //message.setFrom(users.get(session.getId()));
        //message.setContent(message.getContent());
        //message.setTo(message.getTo());
        
        String msgs = jsonUtils.sendMessage(users.get(session.getId()), messages);
        
        String flag = null;
        String mySessionId = null;
        String msg = null;
        
        // Parsing the json and getting message
        try {
            JSONObject jObj = new JSONObject(messages);
            flag = jObj.getString("flag");
            mySessionId = session.getId();
            msg = jObj.getString("message");
            //session.getBasicRemote().sendText(messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        log.info("My log for flag: "+flag.toString());
        JSONObject jObj2 = new JSONObject();
        if(flag.equals("message") || mySessionId.equals(users.get(session.getId()))){
        	
            try {
            	
            	jObj2.put("flag", "self");
				jObj2.put("sessionId", session.getId());
				//jObj.put("name", fromName);
	            jObj2.put("message", msg);
	            //session.getBasicRemote().sendText(jObj2.toString());
	            Message message = new Message();
	            message.setFrom(username);
	            message.setContent(msg);
	            broadcast(message);
	            
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        //session.getBasicRemote().sendText(jObj2.toString());
        //sendMessageToOneUser(message);
        //session.getBasicRemote().sendText(messages);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        log.info(session.getId() + " disconnected!");

        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.warning(throwable.toString());
    }

    
    
    private static void broadcast(Message message) throws IOException, EncodeException {
        for (WebsocketServer endpoint : chatEndpoints) {
            synchronized(endpoint) {
                endpoint.session.getBasicRemote().sendObject(message);
            }
        }
    }
    
    

    private static void sendMessageToOneUser(Message message) throws IOException, EncodeException {
        for (WebsocketServer endpoint : chatEndpoints) {
            synchronized(endpoint) {
            	endpoint.session.getBasicRemote().sendObject(message);
                if (endpoint.session.getId().equals(getSessionId(message.getTo()))) {
                    endpoint.session.getBasicRemote().sendObject(message);
                }
            }
        }
    }

    private static String getSessionId(String to) {
        if (users.containsValue(to)) {
            for (String sessionId: users.keySet()) {
                if (users.get(sessionId).equals(to)) {
                    return sessionId;
                }
            }
        }
        return null;
    }
    
    /**
	 * Method to send message to all clients
	 * 
	 * @param sessionId
	 * @param message
	 *            message to be sent to clients
	 * @param isNewClient
	 *            flag to identify that message is about new person joined
	 * @param isExit
	 *            flag to identify that a person left the conversation
	 * */
	private void sendMessageToAll(String sessionId, String name,
			String message, boolean isNewClient, boolean isExit) {

		// Looping through all the sessions and sending the message individually
		for (Session s : sessions) {
			String json = null;

			// Checking if the message is about new client joined
			if (isNewClient) {
				json = jsonUtils.getNewClientJson(sessionId, name, message,
						sessions.size());

			} else if (isExit) {
				// Checking if the person left the conversation
				json = jsonUtils.getClientExitJson(sessionId, name, message,
						sessions.size());
			} else {
				// Normal chat conversation message
				json = jsonUtils
						.getSendAllMessageJson(sessionId, name, message);
			}

			try {
				System.out.println("Sending Message To: " + sessionId + ", "
						+ json);

				s.getBasicRemote().sendText(json);
			} catch (IOException e) {
				System.out.println("error in sending. " + s.getId() + ", "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
