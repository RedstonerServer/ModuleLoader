package com.redstoner.modules.damnspam;

public class SpamInput {
	
	protected String	player;
	protected double	timeoutOn;
	protected double	timeoutOff;
	protected double	lastTime;
	
	protected SpamInput(String player, double timeoutOff, double timeoutOn, double lastTime) {
		this.player = player;
		this.timeoutOff = timeoutOff;
		this.timeoutOn = timeoutOn;
		this.lastTime = lastTime;
	}
	
}
