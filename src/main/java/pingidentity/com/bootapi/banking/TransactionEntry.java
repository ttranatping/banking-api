package pingidentity.com.bootapi.banking;

class TransactionEntry
{
	private final String toUser;
	private final double amount;
	private final String type;
	private final String desc;
	
	TransactionEntry(String toUser, double amount, String type)
	{
		this.toUser = toUser;
		this.amount = amount;
		this.type = type;
		
		switch(type)
		{
			case "DEPOSIT": this.desc = String.format("%.2f was deposited in your account", amount);
			break;
			case "RECEIVED": this.desc = String.format("%.2f was deposited in your account by %s", amount, toUser);
			break;
			default: this.desc = String.format("%.2f was transferred to %s", amount, toUser);
			break;
		}
	}

	public String getUser() {
		
		return toUser;
	}

	public double getAmount() {
		return amount;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
}