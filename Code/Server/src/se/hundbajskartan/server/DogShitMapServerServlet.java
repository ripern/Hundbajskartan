package se.hundbajskartan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import se.hundbajskartan.server.DogShit;
import se.hundbajskartan.server.PMF;

@SuppressWarnings("serial")
public class DogShitMapServerServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(DogShitMapServerServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");

		//ToDo h�mta alla hundbajsar
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + DogShit.class.getName();
	    List<DogShit> dogShits = (List<DogShit>) pm.newQuery(query).execute();
	    pm.close();
	    
	    //ToDo serialisera och skicka i JSON
	    Gson gson = new Gson();
	    resp.getWriter().println(gson.toJson(dogShits));
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line + "\n");
            line = reader.readLine();
        }
        reader.close();
        String data = sb.toString();
        
        //ToDo avserialisera JSON
        Gson gson = new Gson();
        DogShit dogShit = gson.fromJson(data, DogShit.class);
        
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(dogShit);
		} finally {
			pm.close();
		}
	}
}
