package se.hundbajskartan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class DogShitMapServerServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(DogShitMapServerServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");

		// addDogShit();

		// ToDo h�mta alla hundbajsar
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + DogShitDatabaseObject.class.getName();
		List<DogShitDatabaseObject> dogShits = (List<DogShitDatabaseObject>) pm
				.newQuery(query).execute();

		List<DogShit> dogShits2 = new LinkedList<DogShit>();
		for (DogShitDatabaseObject ds : dogShits) {
			DogShit dogShit = new DogShit(ds.getLongitude(), ds.getLatitude(),
					ds.getDate());
			dogShits2.add(dogShit);
		}

		// ToDo serialisera och skicka i JSON
		Gson gson = new Gson();
		resp.getWriter().println(gson.toJson(dogShits2));
		pm.close();
	}

	@Override
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

		Gson gson = new Gson();
		// Deserialize
		DogShit ds = gson.fromJson(data, DogShit.class);
		// Create DogShit object with key (DogShitDatabaseObject)
		DogShitDatabaseObject dsdo = new DogShitDatabaseObject(ds
				.getLongitude(), ds.getLatitude(), ds.getDate());
		// DogShitDatabaseObject dogShit = gson.fromJson(data,
		// DogShitDatabaseObject.class);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(dsdo);
		} finally {
			pm.close();
		}
	}

	private void addDogShit() {

		DogShitDatabaseObject dogShit = new DogShitDatabaseObject(0.0, 0.0,
				new Date());

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(dogShit);
		} finally {
			pm.close();
		}
	}
}
