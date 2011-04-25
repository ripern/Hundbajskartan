package se.hundbajskartan.server;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class NewDogShit extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");

		addDogShit();
	}

	private void addDogShit() {
		Random random = new Random();
		final int latDecimals = random.nextInt(99);
		final int lngDecimals = random.nextInt(99);

		DogShitDatabaseObject dogShit = new DogShitDatabaseObject(
				18 + (lngDecimals / 100d), 59 + (latDecimals / 100d),
				new Date());

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(dogShit);
		} finally {
			pm.close();
		}
	}
}
