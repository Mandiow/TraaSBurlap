package com.example;

import java.util.LinkedList;
import java.util.List;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;
import burlap.mdp.singleagent.environment.extensions.EnvironmentServerInterface;
import burlap.mdp.singleagent.model.SampleModel;

public class SumoEnvironment implements Environment, EnvironmentServerInterface {
	
	
	/**
	 * Reward given for the current action
	*/
	private int reward = 0;
	//Occupancy in the WE
	private double weOcc;
	//Occupancy in the NS
	private double nsOcc;
	//Which phase is the TL
	private int phase;
	
	/**
	   * Most recent state, to be returned by currentObservation() method
	 */
	SumoState currentObservationState;
	/**
	 * The {@link EnvironmentObserver} objects that will be notified of {@link burlap.mdp.singleagent.environment.Environment}
	 * events.
	 */
	protected List<EnvironmentObserver> observers = new LinkedList<EnvironmentObserver>();
	
	public SumoEnvironment(){
		this.currentObservationState = new SumoState();
	}
	
	public double getWeOcc() {
		return weOcc;
	}

	public void setWeOcc(double weOcc) {
		this.weOcc = weOcc;
	}

	public double getNsOcc() {
		return nsOcc;
	}

	public void setNsOcc(double nsOcc) {
		this.nsOcc = nsOcc;
	}
	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}
	public void setPhase(int newPhase) {
		this.phase = newPhase;
	}
	public SumoState getCurrentObservationState() {
		return currentObservationState;
	}

	public void setCurrentObservationState(SumoState currentObservationState) {
		this.currentObservationState = currentObservationState;
	}

	@Override
	public void addObservers(EnvironmentObserver... observers) {
	}

	@Override
	public void clearAllObservers() {
	}

	@Override
	public void removeObservers(EnvironmentObserver... observers) {
	}

	@Override
	public List<EnvironmentObserver> observers() {
		return null;
	}

	@Override
	public State currentObservation() {
		return currentObservationState;
	}

	@Override
	public EnvironmentOutcome executeAction(Action a) {
		//Learn how to deal with the current and next state
		SumoState newState = makeState(weOcc, nsOcc, phase);
		EnvironmentOutcome eo;
		eo = new EnvironmentOutcome(this.currentObservationState, a, newState, reward, true);
		return eo;
	}

	@Override
	public double lastReward() {
		return reward;
	}

	@Override
	public boolean isInTerminalState() {
		return false;
	}

	@Override
	public void resetEnvironment() {
	}
	/**
	 * This method have the purpose to get the values of the Occupancy in the lanes and, abstract to a
	 * LOW,MID or HIGH Traffic, additionaly, it sets, whith the phase passed as argument, which direction
	 * is open
	 * */
	public SumoState makeState(double newWEOcc, double newNSOcc, int newPhase){
		String nsStatus,weStatus,trafficLight = null;
		if(newNSOcc < .2)
			nsStatus = SumoState.LOW_TRAFFIC;
		else 
			if(newNSOcc < .45)
				nsStatus = SumoState.MID_TRAFFIC;
			else
				nsStatus = SumoState.HIGH_TRAFFIC;
		if(newWEOcc < .2)
			weStatus = SumoState.LOW_TRAFFIC;
		else 
			if(newWEOcc < .45)
				weStatus = SumoState.MID_TRAFFIC;
			else
				weStatus = SumoState.HIGH_TRAFFIC;
		if(newPhase == 0)
			trafficLight = SumoState.WE_OPEN;
		if(newPhase == 2)
			trafficLight = SumoState.NS_OPEN;
		
		return new SumoState(trafficLight,nsStatus,weStatus);
		
	}


}
