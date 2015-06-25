package net.porillo.types;

public enum Option {

	ADMIN_ALERTS(0),
	PLAYER_ALERTS(1),
	LOGGING(2),
	RAIN(3),
	THUNDER(4),
	LIGHTNING(5),
	PVP(6),
	FISHN(7),
	SHOOTING(8),
	ENCHANTING(9),
	FIREWORKS(10),
	TRADING(11),
	NOEXP(12),
	NODROPS(13),
	POTION(14),
	TIME(15),
	EDISABLE(16),
	SDISABLE(17),
	NOMOBARMOR(18),
	PDEATHEXP(19),
	EDEATHEXP(20),
	EDEATHDROPS(21),
	PDEATHITEMS(22),
	PORTAL_CREATE(23),
	TARGET(0),
	INTERVAL(1);

	private final int option;

	Option(int i) {
		this.option = i;
	}

	public int getId() {
		return option;
	}
}
