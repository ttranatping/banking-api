package pingidentity.com.bootapi.banking;

public class InsufficientFundsException extends Exception {
	
	public InsufficientFundsException(String id, Double balance, double amount) {
		super(String.format("%s attempted to transfer %(,.2f but had insufficient funds %(,.2f", id, amount, balance));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
