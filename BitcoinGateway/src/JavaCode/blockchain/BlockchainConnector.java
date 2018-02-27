package JavaCode.blockchain;

public interface BlockchainConnector {

    // Connect
    public Object connectToBlockchain();

    // Disconnect
    public void disconnectFromBlockchain();
    // create wallet
    public void createWallet();
    // Transfer
    public void transferCoins();
    // GetBalance
    public String getBalance();
    // GetHistoryTransactions

    // Listener
    public void addListeners();


}
