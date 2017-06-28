package com.example;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.jfree.data.xy.XYSeries;

import com.ufrgs.topic.handler.BurlapTraci;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.singleagent.Episode;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.statehashing.HashableStateFactory;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Gui;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.CommandProcessor;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
 
public class Main extends JPanel {
 
    static String sumo_bin = "c:/Program Files (x86)/DLR/Sumo/bin/sumo-gui.exe";
    static String config_file = "c:/Users/Mandiow/Documents/SUMOTRACI/3x3Grid/3x3GridHalfRandom.sumocfg";
    static int duration = 259200;
	
    public static void main(String[] args) {
    	//
    	int fiveSecs = 0;
        int stoppedCars = 0;
        int lastStoppedCars = 0;
        double nsOcc;
        double weOcc;
        double gamma = 0.8;
        double learningRate = 1.;
        double qInit = 0.;
        double epsilon = 0.7;
        
    	//The user needs to pass their configuration, sumo path and duration
//        if(args.length > 3 || args.length < 3)
//        	return ;
//    	
//    	sumo_bin= args [0];
//		config_file= args[1];
//		duration = Integer.parseInt(args[2]);
    	PrintWriter writer = null;
    	try {
			writer = new PrintWriter("aes.csv", "UTF-8");
			//writer.println("Time,Queue");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        
        
        //start Simulation
    	BurlapTraci bT = new BurlapTraci();
        SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
        //set some options
        conn.addOption("step-length", "1"); //timestep 1 second
        QLearningTL agent = bT.createAgent(gamma,qInit,learningRate);
        EpsilonGreedy p = (EpsilonGreedy) agent.getLearningPolicy();
        p.setEpsilon(epsilon);
        try{
             
            //start TraCI
            conn.runServer();
            //load routes and initialize the simulation
            conn.do_timestep();
             
            weOcc = bT.getOccupancy(conn, "0Wi") + bT.getOccupancy(conn, "0Ei");
            nsOcc = bT.getOccupancy(conn, "0Ni") + bT.getOccupancy(conn, "0Si");
            int phase = (int)conn.do_job_get(Trafficlight.getPhase("0"));
            bT.initState(bT.getEnv().makeState(weOcc, nsOcc, phase));
            for(int i=10; i<duration; i++){
			    fiveSecs++;
			    stoppedCars = bT.getStoppedVehiclesAmount(conn,"0Si") +
			    			  bT.getStoppedVehiclesAmount(conn,"0Wi") +
			    			  bT.getStoppedVehiclesAmount(conn,"0Ei") +
			    			  bT.getStoppedVehiclesAmount(conn,"0Ni");
			    if(i == 1000){
			    	epsilon = 0.1;
			    	p.setEpsilon(epsilon);
			    }
                if(fiveSecs == 10){
                	bT.getEnv().setNsOcc( bT.getOccupancy(conn, "0Ni")+ bT.getOccupancy(conn, "0Si"));
                	bT.getEnv().setWeOcc( bT.getOccupancy(conn, "0Wi")+ bT.getOccupancy(conn, "0Ei"));
                	bT.getEnv().setPhase((int)conn.do_job_get(Trafficlight.getPhase("0")));
                	bT.getEnv().setReward(lastStoppedCars/fiveSecs);
                	lastStoppedCars = stoppedCars;
                	writer.println(i+","+lastStoppedCars);
                	fiveSecs = 0;
                	stoppedCars = 0;
                	//Noticed something awkward, if i set the step to 2, it never learns
                	Episode e = agent.runLearningEpisode(bT.getEnv(), 1);
                	bT.makeAction(conn,e.actionString());
                }
                else{
                	lastStoppedCars = lastStoppedCars - stoppedCars;
                }
                stoppedCars = 0;
                conn.do_timestep();
            }   
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        finally{
        	//stop TraCI
            conn.close();
            writer.close();
        }
         
    }
 
}