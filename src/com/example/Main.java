package com.example;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Gui;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.CommandProcessor;
import de.tudresden.sumo.util.SumoCommand;
import it.polito.appeal.traci.SumoTraciConnection;
 
public class Main {
 
    static String sumo_bin = "c:/Program Files (x86)/DLR/Sumo/bin/sumo-gui.exe";
    static String config_file = "c:/Users/Mandiow/Documents/SUMOTRACI/3x3Grid/3x3GridHalfRandom.sumocfg";
    static int duration = 3600;
     
    public static void main(String[] args) {
    	//The user needs to pass their configuration, sumo path and duration
//        if(args.length > 3 || args.length < 3)
//        	return ;
//    	
//    	sumo_bin= args [0];
//		config_file= args[1];
//		duration = Integer.parseInt(args[2]);
    	
        //start Simulation
        SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
         
        //set some options
        conn.addOption("step-length", "0.1"); //timestep 1 second
         
        try{
             
            //start TraCI
            conn.runServer();
 
            //load routes and initialize the simulation
            conn.do_timestep();
             
             
            for(int i=10; i<duration; i++){
             
                //build screenshots for every 60 timestep
                if(i%60 == 0){
                    conn.do_job_set(Gui.screenshot("View #0", "screenshot_"+i+".png"));
                }
                 
                //current simulation time
                int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());
                //System.out.println(CommandProcessor.read(type, s));
                //add new vehicle
                Object obj =conn.do_job_get(Lane.getIDCount());
                
                System.out.println(obj);
                conn.do_timestep();
            }
             
             
            //stop TraCI
            conn.close();
             
        }catch(Exception ex){
        	ex.printStackTrace();
        }
         
    }
 
}