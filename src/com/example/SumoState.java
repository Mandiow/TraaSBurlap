package com.example;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;

public class SumoState implements MutableState {
	//Constants
	public final static String NS_OPEN = "0-2 Open";
	public final static String WE_OPEN = "1-3 Open";
	public final static String LOW_TRAFFIC = "Low Amount of Cars";
	public final static String MID_TRAFFIC = "Mid Amount of Cars";
	public final static String HIGH_TRAFFIC = "High Amount of Cars";
	
	/**
	   * String representation of the Traffic Light Open Phase.
	   * For example: "0-2 Open"
	   */
	public static String VAR_KEY_TL_STATUS = "tlStatus";
	/**
	   * String representation of the Occupancy in the given direction.
	   * For example: "Mid Amount of Cars"
	   */
	public static String VAR_KEY_NS_STATUS= "nsStatus";
	public static String VAR_KEY_WE_STATUS = "weStatus";
	
	private final static List<Object> keys =
		      Arrays.asList(VAR_KEY_TL_STATUS, VAR_KEY_NS_STATUS,VAR_KEY_WE_STATUS);
	
	public String tlStatus;
	public String nsStatus;
	public String weStatus;
	
	public SumoState(String tlStatus, String nsStatus, String weStatus) {
		this.tlStatus = tlStatus;
		this.nsStatus = nsStatus;
		this.weStatus = weStatus;
	}
	//Empty Constructor
	public SumoState(){
	}
	//Return the State Keys
	@Override
	public List<Object> variableKeys() {
		return keys;
	}
	/**
	 * Returns the desirable key
	*/
	@Override
	public Object get(Object variableKey) {
		if(variableKey.equals(VAR_KEY_TL_STATUS)){
			return this.tlStatus;
		}
		else 
			if(variableKey.equals(VAR_KEY_NS_STATUS)){
				return this.nsStatus;
			}
			else 
				if(variableKey.equals(VAR_KEY_WE_STATUS)){
					return this.weStatus;
				}
		throw new UnknownKeyException(variableKey);
	}
	//Deep Copy
	@Override
	public SumoState copy() {
		return new SumoState(tlStatus,nsStatus,weStatus);
	}
	//State Setting Status
	@Override
	public MutableState set(Object variableKey, Object value) {
		if(variableKey.equals(VAR_KEY_TL_STATUS)){
			this.tlStatus = (String) value;
		}
		else 
			if(variableKey.equals(VAR_KEY_NS_STATUS)){
				this.nsStatus = (String) value;
			}
			else 
				if(variableKey.equals(VAR_KEY_WE_STATUS)){
					this.weStatus = (String) value;
				}else
					throw new UnknownKeyException(variableKey);
		return this;
	}

}
