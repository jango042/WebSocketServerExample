package server.ws;

import java.util.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;



public class MessageDecoder implements Decoder.Text<Message>{
	
	 private final Logger log = Logger.getLogger(getClass().getName());

	    @Override
	    public Message decode(String s) throws DecodeException {
	        log.info("incoming message : " + s);

	        Gson gson = new Gson();
	        Message message = gson.fromJson(s, Message.class);
	        return message;
	    }

	    @Override
	    public boolean willDecode(String s) {
	        return (s != null);
	    }

	    @Override
	    public void init(EndpointConfig endpointConfig) {
	        // do nothing
	    }

	    @Override
	    public void destroy() {
	        // do nothing
	    }
}
