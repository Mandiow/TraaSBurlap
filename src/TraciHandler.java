import de.tudresden.sumo.cmd.Edge;
import it.polito.appeal.traci.SumoTraciConnection;

public class TraciHandler {
	
	/***
	 * 
	 * @param conn SumoConnection to send the command
	 * @param edge The edge you want to track
	 * @return double with the % of the Occupancy
	 * @throws Exception In the case something goes terribly wrong
	 */
	public double getOccupancy(SumoTraciConnection conn, String edge) throws Exception{
		return (double) conn.do_job_get(Edge.getLastStepOccupancy(edge));	
	}

}
