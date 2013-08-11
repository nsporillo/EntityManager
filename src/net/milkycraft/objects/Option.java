package net.milkycraft.objects;

public enum Option {

	ADMIN_ALERTS(0), PLAYER_ALERTS(1), LOGGING(2), RAIN(3), THUNDER(4), LIGHTNING(5), PVP(
			6), FISHING(7), SHOOTING(8), ENCHANTING(9), FIREWORKS(10), TRADING(11), NOEXP(
			12), NODROPS(13), TIME(14), PDEATHEXP(15), EDEATHEXP(16), EDEATHDROPS(17), PDEATHITEMS(
			18), NOMOBARMOR(19), PORTAL_CREATE(20), TARGET(0), INTERVAL(1);

	private final int option;

	Option(int i) {
		this.option = i;
	}

	public int getId() {
		return option;
	}
}
