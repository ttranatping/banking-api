package pingidentity.com.bootapi.banking;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankingController {

	protected final Log logger = LogFactory.getLog(BankingController.class);
	
    @RequestMapping("/bank/me")
    public UserHistory me() {
    	
    	logger.info("/bank/me being performed");
    	
    	User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
    	String username = user.getUsername();
    	
    	logger.info("User: " + username);
    	
    	UserHistory userHistory = UserHistory.GetUser(username);
    	
        return userHistory;
    }

    @RequestMapping("/bank/deposit")
    public UserHistory deposit(double amount) {
    	
    	logger.info("/bank/deposit being performed");
    	
    	User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
    	String username = user.getUsername();
    	
    	logger.info("User: " + username);
    	
    	UserHistory userHistory = UserHistory.GetUser(username);
    	userHistory.deposit(amount);
    	
        return userHistory;
    }

    @RequestMapping("/bank/getStatement")
    public List<TransactionEntry> getStatement(Integer index) {
    	
    	if(index == null)
    		index = 0;
    	
    	logger.info("/bank/getStatement being performed");
    	
    	User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
    	String username = user.getUsername();
    	
    	logger.info("User: " + username);
    	
    	UserHistory userHistory = UserHistory.GetUser(username);
    	
    	List<TransactionEntry> entries = userHistory.getTransactionEntries();
    	List<TransactionEntry> returnEntries = new ArrayList<TransactionEntry>();
    	
    	while(index < entries.size())
    	{
    		returnEntries.add(entries.get(index));
    		index++;
    	}
    	
        return returnEntries;
    }

    @RequestMapping("/bank/transfer")
    public UserHistory transfer(String toUser, double amount) throws InsufficientFundsException {

    	logger.info("/bank/transfer being performed");
    	
    	User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
    	String username = user.getUsername();
    	
    	logger.info("User: " + username);
    	
    	UserHistory userHistory = UserHistory.GetUser(username);
    	userHistory.send(toUser, amount);
    	
        return userHistory;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(InsufficientFundsException e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }
    
    private static class ErrorResponse {
        @SuppressWarnings("unused")
		public String message;
        @SuppressWarnings("unused")
		public String status = "error";
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
    
    
}