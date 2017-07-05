package com.ufrgs.topic.handler;


import com.example.ChangePhaseActionType;
import com.example.KeepPhaseActionType;
import com.example.QLearningTL;
import com.example.SumoEnvironment;
import com.example.SumoState;
import com.example.TrafficLightAction;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;

public class BurlapTraci {
	//Single Agent Domain
	SADomain domain;
	HashableStateFactory hashingFactory;
	//A setted enviroment for SUMO
	SumoEnvironment env;
	int starvationAvoidance;
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
	public int getStoppedVehiclesAmount(SumoTraciConnection conn, String edge) throws Exception{
		
		int stoppedCars = 0;
		SumoStringList sumoStringList = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs(edge));
	    for(int index = 0; index < sumoStringList.size() ; index++){
			String vehID = sumoStringList.get(index);
			double speed = (double) conn.do_job_get(Vehicle.getSpeed(vehID));
			//Get vehicles that are in a very low speed
			if(speed < 2.7){// 2.7 M/s = approx 10 Km/h
				stoppedCars++;
			}
		}
		return stoppedCars;
	}
	
	public void makeAction(SumoTraciConnection conn, String action) throws Exception{
		if(starvationAvoidance > 150)
			System.out.println(starvationAvoidance);
		if((starvationAvoidance > 10 && action.equals(ChangePhaseActionType.CHANGE_ACTION_NAME)) || starvationAvoidance > 175){
    		/**
    		 * Phase 0 = W - E Open 1 - 3 
    		 * Phase 2 = N - S Open 0 - 2
    		*/
    		if((int)conn.do_job_get(Trafficlight.getPhase("0")) == 0){
    			conn.do_job_set(Trafficlight.setPhase("0",1));
    		}
    		else{
    			conn.do_job_set(Trafficlight.setPhase("0",3));
    		}
    		starvationAvoidance = 0;
    	}
    	else{
    		if((int)conn.do_job_get(Trafficlight.getPhase("0")) == 0){
    			conn.do_job_set(Trafficlight.setPhase("0",0));
    		}
    		else{
    			conn.do_job_set(Trafficlight.setPhase("0",2));
    		}
    		starvationAvoidance +=5;
    	}
	}
	/**
	 * Generate the environment creating a Single Agent domain, Adding our environment
	 * And adding the possible actions to the domain
	 * */
    private void generateEnvironment(){
    	domain = new SADomain();
    	domain.addActionTypes(new UniversalActionType(ChangePhaseActionType.CHANGE_ACTION_NAME),
				new UniversalActionType(KeepPhaseActionType.KEEP_ACTION_NAME));
    	this.hashingFactory = new SimpleHashableStateFactory();
    	env = new SumoEnvironment();
    	this.starvationAvoidance = 0;
    }
    //Getter to 
    public SumoEnvironment getEnv() {
		return env;
	}

	public QLearningTL createAgent(double gamma,double qInit,double learningRate){
		generateEnvironment();
    	return new QLearningTL(domain, 0.99, hashingFactory, 0., 1.);
    }
	public void initState(SumoState initialState) {
		env.setCurrentObservationState(initialState);
	}
	
	
}
