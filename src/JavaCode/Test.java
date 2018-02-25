package JavaCode;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import groovy.lang.Script;
import groovy.ui.SystemOutputInterceptor;
import org.apache.xerces.impl.dv.util.Base64;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.crypto.BIP38PrivateKey;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.bitcoinj.wallet.listeners.*;
import org.bouncycastle.util.encoders.Hex;
import java.security.KeyFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.sql.DriverManager.println;


/**
 * The following example shows you how to create a SendRequest to send coins from a wallet to a given address.
 */
public class Test {

    private static String[] fileNames=null;
    private static NetworkParameters params;
    private static ArrayList<Wallet> walletsList;
    private static double totalBitCoinsFromAll=0;

    public static void main(String[] args) throws Exception  {


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



    public static void  initialiseWalletAppKit(){


        String filePrefix=null;
        WalletAppKit kit;
        Wallet wallet;
        for(int i=0;i< fileNames.length;i++ ){
            kit = new WalletAppKit(params, new File("."), fileNames[i]);
            wallet=new Wallet(params);
            kit.startAsync();
            kit.awaitRunning();
            wallet=kit.wallet();

            wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
                @Override
                public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+
                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+
                            "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+
                            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<"+
                            ">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<>>>>>>>>"+
                            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+
                            coin.toFriendlyString());
                }
            });

            File file = new File("C:\\Users\\vadnu\\eclipse-workspace\\"+fileNames[i]+".wallet");

            Address currentAddress=kit.wallet().freshReceiveAddress();
            wallet.addWatchedAddress(currentAddress);

            System.out.println(currentAddress);
            wallet.getBalance();
            wallet.getPendingTransactions();
            printWalletsInfo(wallet,fileNames[i]);
//
//                if(i==1){
//                    sendFunds(kit,new Address(params,"mudZVu9SFMrBE5WS8cjiYCaVFPXdhrejV7"),Coin.parseCoin("0.2"));
//                }
            try{
                wallet.saveToFile(file);

            }catch(Exception ex){
                ex.printStackTrace();
            }

        }



    }


    public static void printWalletsInfo(Wallet wallet,String walletName){
        Coin avSpend=wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE);
        double available=Double.parseDouble(avSpend.toPlainString());
        totalBitCoinsFromAll+=available;
        for (int i = 0; i <wallet.getWatchedAddresses().size() ; i++) {

            System.out.println(i+">> "+wallet.getWatchedAddresses().get(i));
        }
        DeterministicSeed seed = wallet.getKeyChainSeed();
        System.out.println(walletName+"BALANCE >> "+wallet.getBalance().toFriendlyString());
        System.out.println(walletName+"BALANCE SPENDABLE>> "+wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE).toFriendlyString());
        System.out.println(walletName+"BALANCE ESTIMATED>> "+wallet.getBalance(BalanceType.ESTIMATED ).toFriendlyString());
        System.out.println(walletName+"ALL TRANSACT >> "+wallet.getTransactions(true).size());
        System.out.println(walletName+"PENDING TRANSACT >> "+wallet.getPendingTransactions().size());
        System.out.println("Seed words are: " + Joiner.on(" ").join(seed.getMnemonicCode()));
        getTransactions(wallet);
    }




    private static Set<Transaction> getTransactions(Wallet wallet){
        boolean includeDeadTransactions = true;
        Set<Transaction> transactions = wallet.getTransactions(includeDeadTransactions);

        for(Transaction tr:transactions)
        {
            Coin c=tr.getFee();
            List<TransactionInput> inputs = tr.getInputs();
            List<TransactionOutput> outputs = tr.getOutputs();
            System.out.println("Date And time of this transaction "+tr.getUpdateTime()+" Amount= "+tr.getValue(wallet)+" Fee= "+c);
            for(TransactionOutput out : outputs){
                System.out.println("Sent to>>>> "+out.getAddressFromP2PKHScript(params)+" Amount=" +out.getValue() +" Type= "+getTransactionType(tr));
                System.out.println(out.getAddressFromP2SH(params));
            }

//        for(TransactionInput in : inputs){
//            System.out.println("Recieved from>> "+in.getFromAddress());
//        }

        }
        return transactions;
    }



    private static void sendFunds(WalletAppKit kit,Address targetAddress,Coin amount){
        try{
            kit.wallet().sendCoins(kit.peerGroup(), targetAddress, amount).tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
        }catch(InsufficientMoneyException ex){
            ex.printStackTrace();

        }

    }















    public static void createBitcoinWallet(String walletName){
        WalletAppKit kit=new WalletAppKit(params, new File("."), walletName);
        Wallet wallet;
        wallet=new Wallet(params);
        kit.startAsync();
        kit.awaitRunning();
    }

    private static List<String> getWalletMnemonicCode(Wallet wallet){

        DeterministicSeed seed = wallet.getKeyChainSeed();
        return seed.getMnemonicCode();
    }


    private static String getWalletBalance(Wallet wallet){

        return wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE).toString();
    }

    private static String getWalletTransactionsNumber(Wallet wallet){

        return Integer.toString(wallet.getTransactions(true).size());
    }

    private static String getTransactionDateAndTime(Transaction tx){

        return tx.getUpdateTime().toString();
    }

    private static String getTransactionAmount(Transaction tx,Wallet wallet){

        return tx.getValue(wallet).toString();
    }


    private static String getTransactionFee(Transaction tx){
        return tx.getFee().toString();
    }

    private static String getTransactionStatus(Transaction tx){
        if(tx.isPending())
            return "Pending";
        else
            return "Verified";
    }

    private static String getTransactionType(Transaction tx){

        if(tx.getOutputs().size()>0){
            return "Sent";
        }
        else{
            return "Recieved";
        }
    }


}









