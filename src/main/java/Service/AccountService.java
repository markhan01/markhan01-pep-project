package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account addAccount(Account account) {
        return this.accountDAO.insertAccount(account);
    }

    public Account getAccount(Account account) {
        return this.accountDAO.getAccount(account);
    }
    
    public Account getAccountById(int id) {
        return this.accountDAO.getAccountById(id);
    }
}
