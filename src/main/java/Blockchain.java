import com.google.gson.GsonBuilder;
import java.util.ArrayList;

public class Blockchain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static int difficulty = 5;

    public static void main(String[] args) {
        blockchain.add(new Block("This is the first block", "0"));
        System.out.println("Attempting to mine block 1");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("This is the second block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Attempting to mine block 2");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("This is the third block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Attempting to mine block 3");
        blockchain.get(2).mineBlock(difficulty);

        if (isChainValid())
            System.out.println("\nThe blockchain is valid");
        else
            System.out.println("\nThe blockchain is not valid");

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            // compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hashes are not equal, the current transaction's data has been altered");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous Hashes are not equal, the previous transaction's data has been altered");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block has not been mined");
                return false;
            }
        }
        return true;
    }
}
