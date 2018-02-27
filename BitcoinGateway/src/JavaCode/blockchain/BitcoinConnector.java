package JavaCode.blockchain;

import com.google.common.base.Joiner;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class BitcoinConnector implements BlockchainConnector{

    private static String[] fileNames=null;


    private final String WALLET_TYPE="Bitcoin";
    private String privateKey;
    private float balance;
    private int totalNumberTrans;
    private static NetworkParameters params;
    private static ArrayList<Wallet> walletsList;
    private static double totalBitCoinsFromAll=0;

     private int x=0;

    public BitcoinConnector() {

        try {
            params = TestNet3Params.get();
            fileNames = new String[]{"my-first-wallet", "my-second-wallet", "my-third-wallet", "my-fourth-wallet"};
            walletsList=new ArrayList<>();
            initialiseWalletAppKit();
            System.out.println("TOTAL FROM ALL WALLETS >>"+totalBitCoinsFromAll+" BTC<<<");

        }catch(Exception ex){
            ex.printStackTrace();
        }



    }

    public String getWALLET_TYPE() {
        return WALLET_TYPE;
    }


    public  void  initialiseWalletAppKit(){

//        for(int i=0;i< fileNames.length;i++ ){
//            kit = new WalletAppKit(params, new File("C:\\Sandbox\\BitcoinGateway"), fileNames[i]);
//            wallet=new Wallet(params);
//
//            wallet=kit.wallet();
//
//            wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
//                @Override
//                public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
//                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+
//                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+
//                            "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+
//                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<"+
//                            ">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<>>>>>>>>"+
//                            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+
//                            coin.toFriendlyString());
//                    transaction.setMemo("Recieved");
//                    addNewAddress(wallet);
//
//
//                }
//
//
//            });
//
//
//            wallet.addCoinsSentEventListener(new WalletCoinsSentEventListener() {
//                @Override
//                public void onCoinsSent(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
//                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"+
//                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"+
//                            "COINS SEEEENT\n"+
//                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<\n"+
//                            ">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<>>>>>>>>"+
//                            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV\n"+
//                            coin.toFriendlyString());
//                    transaction.setMemo("Sent");
//                    addNewAddress(wallet);
//
//
//                }
//            });
//
//            File file = new File("C:\\Sandbox\\BitcoinGateway"+fileNames[i]+".wallet");
//
//
//            wallet.getBalance();
//            wallet.getPendingTransactions();
//            printWalletsInfo(wallet,fileNames[i]);
//
////                if(i==2){
////                sendFunds(kit,new Address(params,"mudZVu9SFMrBE5WS8cjiYCaVFPXdhrejV7"),Coin.parseCoin("0.02"));
////            }
//            try{
//                wallet.saveToFile(file);
//
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//
//        }



    }


    private  void addNewAddress(Wallet wallet){
        Address currentAddress=wallet.freshReceiveAddress();
        wallet.addWatchedAddress(currentAddress);
    }


    public  void printWalletsInfo(Wallet wallet,String walletName){
        Coin avSpend=wallet.getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE);
        double available=Double.parseDouble(avSpend.toPlainString());
        totalBitCoinsFromAll+=available;
        for (int i = 0; i <wallet.getWatchedAddresses().size() ; i++) {

            System.out.println(i+">> "+wallet.getWatchedAddresses().get(i));
        }
        DeterministicSeed seed = wallet.getKeyChainSeed();
        System.out.println(walletName+"BALANCE >> "+getWalletBalance(wallet));
        System.out.println(walletName+"ALL TRANSACT >> "+getWalletTransactionsNumber(wallet));
        System.out.println(walletName+"PENDING TRANSACT >> "+getWalletPendingTransactionsNumber(wallet));
        System.out.println("Seed words are: " + Joiner.on(" ").join(getWalletMnemonicCode(wallet)));
        getTransactions(wallet);
    }




    private  Set<Transaction> getTransactions(Wallet wallet){
        boolean includeDeadTransactions = true;
        Set<Transaction> transactions = wallet.getTransactions(includeDeadTransactions);

        for(Transaction tr:transactions)
        {
            Coin c=tr.getFee();
            List<TransactionInput> inputs = tr.getInputs();
            List<TransactionOutput> outputs = tr.getOutputs();
            TransactionOutput out=outputs.get(0);
            System.out.print("Date And time of this transaction "+getTransactionDateAndTime(tr)+" Amount= "+getTransactionAmount(tr,wallet)+" Fee= "+getTransactionFee(tr));

            System.out.println(">>>> "+out.getAddressFromP2PKHScript(params) +" Type= "+getTransactionType(tr));


        }
        return transactions;
    }



    private  void sendFunds(WalletAppKit kit,Address targetAddress,Coin amount){
        try{
            kit.wallet().sendCoins(kit.peerGroup(), targetAddress, amount).tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
        }catch(InsufficientMoneyException ex){
            ex.printStackTrace();

        }

    }















    public  void createBitcoinWallet(String walletName){
        WalletAppKit kit=new WalletAppKit(params, new File("."), walletName);
        Wallet wallet;
        wallet=new Wallet(params);
        kit.startAsync();
        kit.awaitRunning();
    }

    private  List<String> getWalletMnemonicCode(Wallet wallet){

        DeterministicSeed seed = wallet.getKeyChainSeed();
        return seed.getMnemonicCode();
    }


    private  String getWalletBalance(Wallet wallet){

        return wallet.getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE).toString();
    }

    private  String getWalletTransactionsNumber(Wallet wallet){

        return Integer.toString(wallet.getTransactions(true).size());
    }

    private  String getWalletPendingTransactionsNumber(Wallet wallet){

        return Integer.toString(wallet.getPendingTransactions().size());
    }


    private  String getTransactionDateAndTime(Transaction tx){

        return tx.getUpdateTime().toString();
    }

    private  String getTransactionAmount(Transaction tx,Wallet wallet){

        return tx.getValue(wallet).toString();
    }


    private  String getTransactionFee(Transaction tx){
        Coin fee=tx.getFee();
        if(fee!=null)
            return fee.toString();

        else
            return null;
    }

    private  String getTransactionStatus(Transaction tx){
        if(tx.isPending())
            return "Pending";
        else
            return "Verified";
    }

    private  String getTransactionType(Transaction tx) {

        return tx.getMemo();

    }


    @Override
    public Object connectToBlockchain() {

        String seedCode = "juice artist similar gravity candy cart raw swap cave initial mountain roast";
        long creationtime = 1519573021L;
        Wallet wallet;
        Address address;
        DeterministicSeed seed = null;
            String filePrefix = "testWallet";
            WalletAppKit kit;
            kit = new WalletAppKit(params, new File("C:\\Sandbox\\BitcoinGateway"), fileNames[1]);

        try {
            seed = new DeterministicSeed(seedCode,null,"",creationtime);
            wallet = Wallet.fromSeed(params, seed);
             address = wallet.currentReceiveAddress();
            System.out.println("BALANCE >> "+getWalletBalance(wallet));

        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
//            kit.restoreWalletFromSeed();
        kit.startAsync();
        kit.awaitRunning();
        System.out.println("Seed words are: " + Joiner.on(" ").join(getWalletMnemonicCode(kit.wallet())));
        System.out.println("Addr: " + kit.wallet().currentReceiveAddress());
        System.out.println("BALANCE >> "+getWalletBalance(kit.wallet()));
        System.out.println("Time >> "+(kit.wallet().getEarliestKeyCreationTime()));

//        kit.wallet().
//            BlockChain chain = kit.chain();
//            BlockStore bs = chain.getBlockStore();
//            Peer peer = kit.peerGroup().getDownloadPeer();
//        Block b;

        return kit;


    }

    @Override
    public void disconnectFromBlockchain() {

    }

    @Override
    public void createWallet() {

    }

    @Override
    public void transferCoins() {

    }

    @Override
    public String getBalance() {
        return null;
    }

    @Override
    public void addListeners() {

    }
}
