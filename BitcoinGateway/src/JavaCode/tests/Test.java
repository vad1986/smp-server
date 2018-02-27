package JavaCode.tests;
import JavaCode.blockchain.BitcoinConnector;


/**
 * The following example shows you how to create a SendRequest to send coins from a wallet to a given address.
 */
public class Test {



    public static void main(String[] args) throws Exception  {
        BitcoinConnector bitCoinWallet=new BitcoinConnector();
        bitCoinWallet.connectToBlockchain();

    }


}









