package pingidentity.com.bootapi.banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHistory {

	private static final HashMap<String, UserHistory> UserHistoryList = new HashMap<String, UserHistory>();
	
    private final String id;
    private Double balance;
    
    private final List<TransactionEntry> transactionEntries;
    
    public static UserHistory GetUser(String id)
    {
    	synchronized(UserHistoryList)
    	{
    		if(!UserHistoryList.containsKey(id) || UserHistoryList.get(id) == null)
    		{
    			UserHistory newUser = new UserHistory(id);
    			UserHistoryList.put(id, newUser);
    			return newUser;
    		}
    		else
    		{
    			return UserHistoryList.get(id);
    		}
    	}
    }
    
    protected static void ClearTransactions()
    {
    	synchronized(UserHistoryList)
    	{
    		UserHistoryList.clear();
    	}
    }

    private UserHistory(String id) {
        this.id = id;
        this.setBalance(0);
        this.transactionEntries = new ArrayList<TransactionEntry>();
    }

    public String getId() {
        return id;
    }
    
    public double getBalance() {
		return balance;
	}

	private void setBalance(double balance) {
		this.balance = balance;
	}

	public List<TransactionEntry> getTransactionEntries() {
		return transactionEntries;
	}
	
	public void send(String toUser, double amount) throws InsufficientFundsException
	{
		synchronized(balance)
		{
			if(balance < amount)
				throw new InsufficientFundsException(this.id, this.balance, amount);
			
			balance -= amount;
		}
		
		TransactionEntry entry = new TransactionEntry(toUser, amount, "SENT");
		this.getTransactionEntries().add(entry);
		
		UserHistory toUserObj = UserHistory.GetUser(toUser);
		toUserObj.receive(this.id, amount);
		
	}
	
	protected void receive(String fromUser, double amount)
	{
		synchronized(balance)
		{
			balance += amount;
		}
		
		TransactionEntry entry = new TransactionEntry(fromUser, amount, "RECEIVED");
		this.getTransactionEntries().add(entry);
	}
	
	public void deposit(double amount)
	{
		synchronized(balance)
		{
			balance += amount;
		}

		TransactionEntry entry = new TransactionEntry(this.id, amount, "DEPOSIT");
		this.getTransactionEntries().add(entry);
	}
}