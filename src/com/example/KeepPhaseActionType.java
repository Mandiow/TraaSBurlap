package com.example;

import java.util.List;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;

public class KeepPhaseActionType {
	public final static String KEEP_ACTION_NAME = "keepAction";
	public String typeName() {
		return KEEP_ACTION_NAME;
	}

}
