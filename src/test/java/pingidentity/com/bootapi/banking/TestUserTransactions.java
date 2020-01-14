package pingidentity.com.bootapi.banking;

import java.util.List;

import pingidentity.com.bootapi.banking.InsufficientFundsException;
import pingidentity.com.bootapi.banking.TransactionEntry;
import pingidentity.com.bootapi.banking.UserHistory;
import junit.framework.TestCase;

public class TestUserTransactions extends TestCase {

	public void testDeposit()
	{
		UserHistory.ClearTransactions();
		
		deposit("bob", 54.55);
		
		UserHistory bob = UserHistory.GetUser("bob");
		List<TransactionEntry> bobTransactions = bob.getTransactionEntries();
		
		assertEquals(54.55, bob.getBalance());
		assertEquals(1, bobTransactions.size());
		assertEquals("DEPOSIT", bobTransactions.get(0).getType());
		assertEquals(54.55, bobTransactions.get(0).getAmount());
		assertEquals("bob", bobTransactions.get(0).getUser());
	}
	
	public void testTransfer() throws InsufficientFundsException
	{
		UserHistory.ClearTransactions();
		
		deposit("bob", 100);
		transfer("bob", "jane", 40);

		UserHistory bob = UserHistory.GetUser("bob");
		List<TransactionEntry> bobTransactions = bob.getTransactionEntries();
		UserHistory jane = UserHistory.GetUser("jane");
		List<TransactionEntry> janeTransactions = jane.getTransactionEntries();

		assertEquals(60.0, bob.getBalance());
		assertEquals(40.0, jane.getBalance());

		assertEquals("DEPOSIT", bobTransactions.get(0).getType());
		assertEquals("SENT", bobTransactions.get(1).getType());
		assertEquals(40.0, bobTransactions.get(1).getAmount());
		assertEquals("jane", bobTransactions.get(1).getUser());
		
		assertEquals("RECEIVED", janeTransactions.get(0).getType());
		assertEquals(40.0, janeTransactions.get(0).getAmount());
		assertEquals("bob", janeTransactions.get(0).getUser());
	}
	
	public void testInsufficientFunds()
	{
		UserHistory.ClearTransactions();
		
		deposit("bob", 100);
		
		InsufficientFundsException result = null;
		
		try {
			transfer("bob", "jane", 140);
		} catch (InsufficientFundsException e) {
			result = e;
		}
		
		assertNotNull(result);
	}
	
	private void deposit(String userId, double amount)
	{
		UserHistory user = UserHistory.GetUser(userId);
		user.deposit(amount);
	}
	
	private void transfer(String fromUser, String toUser, double amount) throws InsufficientFundsException
	{
		UserHistory bob = UserHistory.GetUser(fromUser);
		bob.send(toUser, amount);
	}
}
